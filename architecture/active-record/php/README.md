# Active Record 패턴 - PHP/Laravel 구현

## 개요

이 문서는 PHP와 Laravel Eloquent ORM을 사용한 Active Record 패턴의 실전 구현 예제를 제공합니다. Laravel의 Eloquent는 Active Record 패턴을 우아하고 강력하게 구현한 대표적인 ORM입니다.

## 환경 설정

### 요구사항
- PHP 8.1+
- Laravel 10.x+
- MySQL/PostgreSQL

### 설치

```bash
# Laravel 프로젝트 생성
composer create-project laravel/laravel active-record-example

# 데이터베이스 마이그레이션
php artisan migrate
```

## 기본 예제: 블로그 시스템

### 1. 데이터베이스 스키마

```mermaid
erDiagram
    users ||--o{ posts : writes
    users ||--o{ comments : writes
    posts ||--o{ comments : has
    posts }o--o{ tags : has

    users {
        bigint id PK
        string name
        string email UK
        string password
        enum role
        timestamp email_verified_at
        timestamps
    }

    posts {
        bigint id PK
        bigint user_id FK
        string title
        text content
        enum status
        timestamp published_at
        timestamps
    }

    comments {
        bigint id PK
        bigint post_id FK
        bigint user_id FK
        text content
        timestamps
    }

    tags {
        bigint id PK
        string name UK
        string slug UK
        timestamps
    }

    post_tag {
        bigint post_id FK
        bigint tag_id FK
    }
```

### 2. 마이그레이션 생성

```php
<?php

// database/migrations/xxxx_create_users_table.php
use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up()
    {
        Schema::create('users', function (Blueprint $table) {
            $table->id();
            $table->string('name');
            $table->string('email')->unique();
            $table->string('password');
            $table->enum('role', ['user', 'author', 'admin'])->default('user');
            $table->timestamp('email_verified_at')->nullable();
            $table->rememberToken();
            $table->timestamps();
        });
    }

    public function down()
    {
        Schema::dropIfExists('users');
    }
};

// database/migrations/xxxx_create_posts_table.php
return new class extends Migration
{
    public function up()
    {
        Schema::create('posts', function (Blueprint $table) {
            $table->id();
            $table->foreignId('user_id')->constrained()->onDelete('cascade');
            $table->string('title');
            $table->string('slug')->unique();
            $table->text('content');
            $table->enum('status', ['draft', 'published', 'archived'])->default('draft');
            $table->timestamp('published_at')->nullable();
            $table->timestamps();

            $table->index(['status', 'published_at']);
        });
    }

    public function down()
    {
        Schema::dropIfExists('posts');
    }
};

// database/migrations/xxxx_create_comments_table.php
return new class extends Migration
{
    public function up()
    {
        Schema::create('comments', function (Blueprint $table) {
            $table->id();
            $table->foreignId('post_id')->constrained()->onDelete('cascade');
            $table->foreignId('user_id')->constrained()->onDelete('cascade');
            $table->text('content');
            $table->timestamps();

            $table->index('post_id');
        });
    }

    public function down()
    {
        Schema::dropIfExists('comments');
    }
};

// database/migrations/xxxx_create_tags_table.php
return new class extends Migration
{
    public function up()
    {
        Schema::create('tags', function (Blueprint $table) {
            $table->id();
            $table->string('name')->unique();
            $table->string('slug')->unique();
            $table->timestamps();
        });

        Schema::create('post_tag', function (Blueprint $table) {
            $table->foreignId('post_id')->constrained()->onDelete('cascade');
            $table->foreignId('tag_id')->constrained()->onDelete('cascade');
            $table->primary(['post_id', 'tag_id']);
        });
    }

    public function down()
    {
        Schema::dropIfExists('post_tag');
        Schema::dropIfExists('tags');
    }
};
```

### 3. Active Record 모델 구현

#### User 모델

```php
<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Foundation\Auth\User as Authenticatable;
use Illuminate\Notifications\Notifiable;
use Laravel\Sanctum\HasApiTokens;

class User extends Authenticatable
{
    use HasApiTokens, HasFactory, Notifiable;

    /**
     * Mass assignment로부터 보호할 속성
     */
    protected $fillable = [
        'name',
        'email',
        'password',
        'role',
    ];

    /**
     * 숨길 속성 (JSON 변환 시)
     */
    protected $hidden = [
        'password',
        'remember_token',
    ];

    /**
     * 타입 캐스팅
     */
    protected $casts = [
        'email_verified_at' => 'datetime',
        'password' => 'hashed', // Laravel 10+
    ];

    /**
     * 관계: User는 여러 Post를 가짐
     */
    public function posts()
    {
        return $this->hasMany(Post::class);
    }

    /**
     * 관계: User는 여러 Comment를 작성
     */
    public function comments()
    {
        return $this->hasMany(Comment::class);
    }

    /**
     * 비즈니스 로직: 관리자 권한 확인
     */
    public function isAdmin(): bool
    {
        return $this->role === 'admin';
    }

    /**
     * 비즈니스 로직: 작성자 권한 확인
     */
    public function isAuthor(): bool
    {
        return in_array($this->role, ['author', 'admin']);
    }

    /**
     * 비즈니스 로직: 이메일 인증 여부
     */
    public function hasVerifiedEmail(): bool
    {
        return !is_null($this->email_verified_at);
    }

    /**
     * 스코프: 활성 사용자만 조회
     */
    public function scopeActive($query)
    {
        return $query->whereNotNull('email_verified_at');
    }

    /**
     * 스코프: 관리자만 조회
     */
    public function scopeAdmins($query)
    {
        return $query->where('role', 'admin');
    }

    /**
     * 스코프: 작성자만 조회
     */
    public function scopeAuthors($query)
    {
        return $query->whereIn('role', ['author', 'admin']);
    }

    /**
     * Accessor: 짧은 이름 (첫 단어만)
     */
    public function getShortNameAttribute(): string
    {
        return explode(' ', $this->name)[0];
    }

    /**
     * 모델 이벤트
     */
    protected static function booted()
    {
        // 사용자 생성 시
        static::created(function ($user) {
            // 환영 이메일 발송 등
            \Log::info("New user created: {$user->email}");
        });

        // 사용자 삭제 시
        static::deleting(function ($user) {
            // 관련 데이터 정리
            $user->posts()->delete();
            $user->comments()->delete();
        });
    }
}
```

#### Post 모델

```php
<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Support\Str;

class Post extends Model
{
    use HasFactory;

    /**
     * Mass assignment 가능한 속성
     */
    protected $fillable = [
        'user_id',
        'title',
        'slug',
        'content',
        'status',
        'published_at',
    ];

    /**
     * 타입 캐스팅
     */
    protected $casts = [
        'published_at' => 'datetime',
    ];

    /**
     * 관계: Post는 한 명의 User에게 속함
     */
    public function author()
    {
        return $this->belongsTo(User::class, 'user_id');
    }

    /**
     * 관계: Post는 여러 Comment를 가짐
     */
    public function comments()
    {
        return $this->hasMany(Comment::class);
    }

    /**
     * 관계: Post는 여러 Tag를 가짐 (다대다)
     */
    public function tags()
    {
        return $this->belongsToMany(Tag::class);
    }

    /**
     * 비즈니스 로직: 게시 여부 확인
     */
    public function isPublished(): bool
    {
        return $this->status === 'published'
            && $this->published_at !== null
            && $this->published_at->isPast();
    }

    /**
     * 비즈니스 로직: 초안 여부 확인
     */
    public function isDraft(): bool
    {
        return $this->status === 'draft';
    }

    /**
     * 비즈니스 로직: 게시
     */
    public function publish(): self
    {
        $this->status = 'published';
        $this->published_at = $this->published_at ?? now();
        $this->save();

        // 이벤트 발생
        event(new \App\Events\PostPublished($this));

        return $this;
    }

    /**
     * 비즈니스 로직: 초안으로 되돌리기
     */
    public function unpublish(): self
    {
        $this->status = 'draft';
        $this->save();

        return $this;
    }

    /**
     * 비즈니스 로직: 아카이브
     */
    public function archive(): self
    {
        $this->status = 'archived';
        $this->save();

        return $this;
    }

    /**
     * 비즈니스 로직: 특정 사용자가 작성자인지 확인
     */
    public function isAuthoredBy(User $user): bool
    {
        return $this->user_id === $user->id;
    }

    /**
     * 스코프: 게시된 포스트만
     */
    public function scopePublished($query)
    {
        return $query->where('status', 'published')
            ->whereNotNull('published_at')
            ->where('published_at', '<=', now());
    }

    /**
     * 스코프: 초안만
     */
    public function scopeDraft($query)
    {
        return $query->where('status', 'draft');
    }

    /**
     * 스코프: 특정 작성자의 포스트
     */
    public function scopeByAuthor($query, User $author)
    {
        return $query->where('user_id', $author->id);
    }

    /**
     * 스코프: 최근 N일 이내
     */
    public function scopeRecent($query, int $days = 30)
    {
        return $query->where('created_at', '>', now()->subDays($days));
    }

    /**
     * 스코프: 인기 포스트 (댓글 많은 순)
     */
    public function scopePopular($query)
    {
        return $query->withCount('comments')
            ->orderBy('comments_count', 'desc');
    }

    /**
     * Accessor: 발췌 (처음 100자)
     */
    public function getExcerptAttribute(): string
    {
        return Str::limit(strip_tags($this->content), 100);
    }

    /**
     * Accessor: 읽는 시간 추정 (분)
     */
    public function getReadingTimeAttribute(): int
    {
        $words = str_word_count(strip_tags($this->content));
        return ceil($words / 200); // 분당 200단어 가정
    }

    /**
     * Mutator: 제목 설정 시 자동으로 slug 생성
     */
    public function setTitleAttribute($value)
    {
        $this->attributes['title'] = $value;

        if (!isset($this->attributes['slug'])) {
            $this->attributes['slug'] = Str::slug($value);
        }
    }

    /**
     * 모델 이벤트
     */
    protected static function booted()
    {
        // 생성 시 slug가 없으면 자동 생성
        static::creating(function ($post) {
            if (empty($post->slug)) {
                $post->slug = Str::slug($post->title);
            }
        });

        // 삭제 시 관련 댓글도 삭제 (cascade 대신 이벤트로도 가능)
        static::deleting(function ($post) {
            $post->comments()->delete();
        });
    }
}
```

#### Comment 모델

```php
<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Comment extends Model
{
    use HasFactory;

    protected $fillable = [
        'post_id',
        'user_id',
        'content',
    ];

    /**
     * 관계: Comment는 한 Post에 속함
     */
    public function post()
    {
        return $this->belongsTo(Post::class);
    }

    /**
     * 관계: Comment는 한 User가 작성
     */
    public function author()
    {
        return $this->belongsTo(User::class, 'user_id');
    }

    /**
     * 비즈니스 로직: 특정 사용자가 작성자인지 확인
     */
    public function isAuthoredBy(User $user): bool
    {
        return $this->user_id === $user->id;
    }

    /**
     * 스코프: 특정 포스트의 댓글
     */
    public function scopeForPost($query, Post $post)
    {
        return $query->where('post_id', $post->id);
    }

    /**
     * 스코프: 최근 댓글
     */
    public function scopeRecent($query)
    {
        return $query->orderBy('created_at', 'desc');
    }
}
```

#### Tag 모델

```php
<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Support\Str;

class Tag extends Model
{
    use HasFactory;

    protected $fillable = ['name', 'slug'];

    /**
     * 관계: Tag는 여러 Post를 가짐 (다대다)
     */
    public function posts()
    {
        return $this->belongsToMany(Post::class);
    }

    /**
     * Mutator: 이름 설정 시 자동으로 slug 생성
     */
    public function setNameAttribute($value)
    {
        $this->attributes['name'] = $value;
        $this->attributes['slug'] = Str::slug($value);
    }

    /**
     * 스코프: 인기 태그 (포스트 많은 순)
     */
    public function scopePopular($query)
    {
        return $query->withCount('posts')
            ->orderBy('posts_count', 'desc');
    }
}
```

## 사용 예제

### 1. CRUD 작업

```php
<?php

// ============================================
// CREATE (생성)
// ============================================

// 방법 1: create 메서드 (mass assignment)
$user = User::create([
    'name' => 'John Doe',
    'email' => 'john@example.com',
    'password' => 'secret123',
    'role' => 'author',
]);

// 방법 2: new + save
$user = new User();
$user->name = 'Jane Doe';
$user->email = 'jane@example.com';
$user->password = 'secret123';
$user->role = 'author';
$user->save();

// 방법 3: firstOrCreate (있으면 조회, 없으면 생성)
$user = User::firstOrCreate(
    ['email' => 'john@example.com'],
    ['name' => 'John Doe', 'password' => 'secret123']
);

// 방법 4: updateOrCreate (있으면 업데이트, 없으면 생성)
$user = User::updateOrCreate(
    ['email' => 'john@example.com'],
    ['name' => 'John Doe Updated']
);

// ============================================
// READ (조회)
// ============================================

// 기본 조회
$user = User::find(1);
$user = User::findOrFail(1); // 없으면 404 예외
$users = User::all();

// 조건부 조회
$user = User::where('email', 'john@example.com')->first();
$users = User::where('role', 'author')->get();
$admins = User::where('role', 'admin')->get();

// 스코프 사용
$activeUsers = User::active()->get();
$authors = User::authors()->get();

// 복잡한 쿼리
$users = User::where('role', 'author')
    ->where('created_at', '>', now()->subMonths(6))
    ->orderBy('name')
    ->limit(10)
    ->get();

// 페이지네이션
$users = User::paginate(15);
$users = User::simplePaginate(15);

// ============================================
// UPDATE (수정)
// ============================================

// 방법 1: find + save
$user = User::find(1);
$user->name = 'Updated Name';
$user->save();

// 방법 2: update 메서드
User::where('id', 1)->update(['name' => 'Updated Name']);

// 방법 3: 대량 업데이트
User::where('role', 'user')
    ->where('created_at', '<', now()->subYear())
    ->update(['status' => 'inactive']);

// 방법 4: 증가/감소
$user->increment('login_count');
$user->decrement('credits', 5);

// ============================================
// DELETE (삭제)
// ============================================

// 방법 1: find + delete
$user = User::find(1);
$user->delete();

// 방법 2: destroy 메서드
User::destroy(1);
User::destroy([1, 2, 3]);

// 방법 3: 조건부 삭제
User::where('status', 'inactive')->delete();

// Soft Delete (소프트 삭제)
// Model에 SoftDeletes trait 추가 필요
$user->delete(); // deleted_at 타임스탬프 설정
$user->restore(); // 복원
$user->forceDelete(); // 영구 삭제

// 소프트 삭제된 레코드 포함
$users = User::withTrashed()->get();
$users = User::onlyTrashed()->get();
```

### 2. 관계 작업

```php
<?php

// ============================================
// 관계 조회
// ============================================

// User의 모든 Post 조회
$user = User::find(1);
$posts = $user->posts; // Collection<Post>

// Post의 작성자 조회
$post = Post::find(1);
$author = $post->author; // User

// Post의 모든 Comment 조회
$comments = $post->comments; // Collection<Comment>

// Post의 모든 Tag 조회
$tags = $post->tags; // Collection<Tag>

// ============================================
// Eager Loading (N+1 문제 해결)
// ============================================

// ❌ N+1 문제 발생
$posts = Post::all();
foreach ($posts as $post) {
    echo $post->author->name; // 각 포스트마다 쿼리 실행
}

// ✅ Eager Loading
$posts = Post::with('author')->get();
foreach ($posts as $post) {
    echo $post->author->name; // 이미 로드됨
}

// 여러 관계 로딩
$posts = Post::with(['author', 'comments', 'tags'])->get();

// 중첩 관계 로딩
$posts = Post::with(['comments.author'])->get();

// 조건부 Eager Loading
$posts = Post::with(['comments' => function ($query) {
    $query->orderBy('created_at', 'desc')->limit(5);
}])->get();

// ============================================
// 관계 생성
// ============================================

// User의 새 Post 생성
$user = User::find(1);
$post = $user->posts()->create([
    'title' => 'New Post',
    'content' => 'Content here...',
    'status' => 'draft',
]);

// 또는
$post = new Post([
    'title' => 'New Post',
    'content' => 'Content here...',
]);
$user->posts()->save($post);

// Post에 Comment 추가
$post = Post::find(1);
$comment = $post->comments()->create([
    'user_id' => auth()->id(),
    'content' => 'Great post!',
]);

// ============================================
// 다대다 관계 (Many-to-Many)
// ============================================

// Post에 Tag 연결
$post = Post::find(1);
$post->tags()->attach([1, 2, 3]); // tag_id 1, 2, 3 연결

// 기존 연결 제거하고 새로 연결
$post->tags()->sync([1, 2, 3]);

// 연결 제거
$post->tags()->detach([1, 2]); // tag_id 1, 2 제거
$post->tags()->detach(); // 모든 태그 제거

// 토글 (있으면 제거, 없으면 추가)
$post->tags()->toggle([1, 2, 3]);

// 피벗 데이터와 함께 저장
$post->tags()->attach(1, ['created_by' => auth()->id()]);

// ============================================
// 관계 쿼리
// ============================================

// 관계가 있는 레코드만 조회
$users = User::has('posts')->get(); // 포스트가 있는 사용자
$users = User::has('posts', '>=', 5)->get(); // 포스트가 5개 이상

// 관계 조건
$users = User::whereHas('posts', function ($query) {
    $query->where('status', 'published');
})->get();

// 관계가 없는 레코드 조회
$users = User::doesntHave('posts')->get();

// 관계 카운트
$users = User::withCount('posts')->get();
foreach ($users as $user) {
    echo "{$user->name} has {$user->posts_count} posts";
}
```

### 3. 스코프와 쿼리 빌더

```php
<?php

// ============================================
// 스코프 사용
// ============================================

// 게시된 포스트만
$posts = Post::published()->get();

// 최근 게시된 포스트
$posts = Post::published()->recent(7)->get();

// 특정 작성자의 게시된 포스트
$user = User::find(1);
$posts = Post::published()->byAuthor($user)->get();

// 인기 포스트
$posts = Post::published()->popular()->take(10)->get();

// 스코프 체이닝
$posts = Post::published()
    ->recent(30)
    ->with('author', 'tags')
    ->orderBy('published_at', 'desc')
    ->paginate(15);

// ============================================
// 쿼리 빌더
// ============================================

// 기본 WHERE 절
$posts = Post::where('status', 'published')->get();
$posts = Post::where('views', '>', 1000)->get();

// 다중 WHERE 조건
$posts = Post::where('status', 'published')
    ->where('user_id', 1)
    ->get();

// OR 조건
$posts = Post::where('status', 'published')
    ->orWhere('status', 'featured')
    ->get();

// WHERE IN
$posts = Post::whereIn('status', ['published', 'featured'])->get();
$posts = Post::whereNotIn('status', ['draft', 'archived'])->get();

// NULL 체크
$posts = Post::whereNull('published_at')->get();
$posts = Post::whereNotNull('published_at')->get();

// 날짜 쿼리
$posts = Post::whereDate('created_at', '2024-01-01')->get();
$posts = Post::whereMonth('created_at', 12)->get();
$posts = Post::whereYear('created_at', 2024)->get();

// BETWEEN
$posts = Post::whereBetween('views', [100, 1000])->get();

// LIKE 검색
$posts = Post::where('title', 'like', '%Laravel%')->get();

// JSON 컬럼 쿼리 (PostgreSQL, MySQL 8.0+)
$users = User::where('preferences->theme', 'dark')->get();

// 정렬
$posts = Post::orderBy('published_at', 'desc')->get();
$posts = Post::orderBy('views', 'desc')
    ->orderBy('created_at', 'desc')
    ->get();

// 랜덤
$posts = Post::inRandomOrder()->take(5)->get();

// 그룹화
$stats = Post::select('user_id')
    ->selectRaw('COUNT(*) as post_count')
    ->groupBy('user_id')
    ->having('post_count', '>', 5)
    ->get();

// 조인
$posts = Post::join('users', 'posts.user_id', '=', 'users.id')
    ->select('posts.*', 'users.name as author_name')
    ->get();
```

### 4. 비즈니스 로직 예제

```php
<?php

// ============================================
// 포스트 게시 워크플로우
// ============================================

class PostController extends Controller
{
    public function publish(Post $post)
    {
        // 권한 확인
        if (!$post->isAuthoredBy(auth()->user()) && !auth()->user()->isAdmin()) {
            abort(403, 'Unauthorized');
        }

        // 비즈니스 로직 실행
        $post->publish();

        return redirect()
            ->route('posts.show', $post)
            ->with('success', 'Post published successfully!');
    }

    public function store(Request $request)
    {
        $validated = $request->validate([
            'title' => 'required|max:255',
            'content' => 'required',
            'tags' => 'array',
        ]);

        // 트랜잭션으로 묶기
        $post = DB::transaction(function () use ($validated) {
            // Post 생성
            $post = auth()->user()->posts()->create([
                'title' => $validated['title'],
                'content' => $validated['content'],
                'status' => 'draft',
            ]);

            // 태그 연결
            if (!empty($validated['tags'])) {
                $post->tags()->attach($validated['tags']);
            }

            return $post;
        });

        return redirect()
            ->route('posts.edit', $post)
            ->with('success', 'Post created successfully!');
    }
}

// ============================================
// 사용자 통계
// ============================================

class UserStatsService
{
    public function getUserStats(User $user): array
    {
        return [
            'total_posts' => $user->posts()->count(),
            'published_posts' => $user->posts()->published()->count(),
            'draft_posts' => $user->posts()->draft()->count(),
            'total_comments' => $user->comments()->count(),
            'posts_views' => $user->posts()->sum('views'),
            'recent_posts' => $user->posts()
                ->published()
                ->latest('published_at')
                ->take(5)
                ->get(),
        ];
    }
}

// ============================================
// 댓글 관리
// ============================================

class CommentService
{
    public function addComment(Post $post, User $user, string $content): Comment
    {
        $comment = $post->comments()->create([
            'user_id' => $user->id,
            'content' => $content,
        ]);

        // 포스트 작성자에게 알림
        $post->author->notify(new NewCommentNotification($comment));

        return $comment;
    }

    public function getPostComments(Post $post, int $perPage = 20)
    {
        return $post->comments()
            ->with('author')
            ->latest()
            ->paginate($perPage);
    }
}
```

## 고급 기능

### 1. 모델 이벤트 및 옵저버

```php
<?php

// ============================================
// Observer 클래스
// ============================================

namespace App\Observers;

use App\Models\Post;
use Illuminate\Support\Facades\Cache;

class PostObserver
{
    /**
     * 생성되기 전
     */
    public function creating(Post $post)
    {
        // slug가 없으면 자동 생성
        if (empty($post->slug)) {
            $post->slug = \Str::slug($post->title);
        }
    }

    /**
     * 생성된 후
     */
    public function created(Post $post)
    {
        \Log::info("New post created: {$post->title}");

        // 캐시 무효화
        Cache::tags('posts')->flush();
    }

    /**
     * 업데이트되기 전
     */
    public function updating(Post $post)
    {
        // 상태가 변경되는 경우
        if ($post->isDirty('status')) {
            \Log::info("Post status changing: {$post->getOriginal('status')} -> {$post->status}");
        }
    }

    /**
     * 업데이트된 후
     */
    public function updated(Post $post)
    {
        // 캐시 무효화
        Cache::forget("post:{$post->id}");
    }

    /**
     * 삭제되기 전
     */
    public function deleting(Post $post)
    {
        // 관련 데이터 정리
        $post->comments()->delete();
        $post->tags()->detach();
    }

    /**
     * 삭제된 후
     */
    public function deleted(Post $post)
    {
        \Log::info("Post deleted: {$post->title}");

        // 캐시 무효화
        Cache::forget("post:{$post->id}");
        Cache::tags('posts')->flush();
    }
}

// ============================================
// Observer 등록 (AppServiceProvider)
// ============================================

namespace App\Providers;

use App\Models\Post;
use App\Observers\PostObserver;
use Illuminate\Support\ServiceProvider;

class AppServiceProvider extends ServiceProvider
{
    public function boot()
    {
        Post::observe(PostObserver::class);
    }
}
```

### 2. Accessor, Mutator, Casts

```php
<?php

class Post extends Model
{
    /**
     * 속성 캐스팅
     */
    protected $casts = [
        'published_at' => 'datetime',
        'is_featured' => 'boolean',
        'meta' => 'array', // JSON 컬럼
        'settings' => 'object',
        'views' => 'integer',
    ];

    /**
     * Accessor: content를 가져올 때 자동으로 마크다운 변환
     */
    public function getContentHtmlAttribute(): string
    {
        return \Str::markdown($this->content);
    }

    /**
     * Accessor: 발췌
     */
    public function getExcerptAttribute(): string
    {
        return \Str::limit(strip_tags($this->content_html), 200);
    }

    /**
     * Accessor: URL
     */
    public function getUrlAttribute(): string
    {
        return route('posts.show', $this->slug);
    }

    /**
     * Accessor: 사람이 읽기 쉬운 날짜
     */
    public function getPublishedAtHumanAttribute(): string
    {
        return $this->published_at?->diffForHumans() ?? 'Not published';
    }

    /**
     * Mutator: 제목 설정 시 자동으로 정리
     */
    public function setTitleAttribute($value)
    {
        $this->attributes['title'] = trim($value);

        // slug도 자동 생성
        if (empty($this->attributes['slug'])) {
            $this->attributes['slug'] = \Str::slug($value);
        }
    }

    /**
     * Mutator: 내용 설정 시 XSS 방지
     */
    public function setContentAttribute($value)
    {
        // HTML 정리 (필요한 경우)
        $this->attributes['content'] = clean($value);
    }
}

// 사용 예제
$post = Post::find(1);
echo $post->content_html; // 마크다운 → HTML 변환됨
echo $post->excerpt; // 자동으로 발췌 생성
echo $post->url; // 전체 URL 반환
echo $post->published_at_human; // "2 days ago"

$post->title = '  My New Post  '; // 자동으로 trim되고 slug 생성됨
$post->save();
```

### 3. 쿼리 최적화

```php
<?php

// ============================================
// N+1 문제 해결
// ============================================

// ❌ N+1 문제 (1 + N개의 쿼리)
$posts = Post::all();
foreach ($posts as $post) {
    echo $post->author->name; // N번의 추가 쿼리
}

// ✅ Eager Loading (2개의 쿼리)
$posts = Post::with('author')->get();
foreach ($posts as $post) {
    echo $post->author->name; // 이미 로드됨
}

// ✅ Lazy Eager Loading (이미 로드된 경우)
$posts = Post::all();
$posts->load('author'); // 필요할 때 로드

// ============================================
// Select 최적화
// ============================================

// ❌ 모든 컬럼 가져오기
$posts = Post::all();

// ✅ 필요한 컬럼만 선택
$posts = Post::select('id', 'title', 'slug', 'published_at')->get();

// ============================================
// 카운트 최적화
// ============================================

// ❌ 모든 댓글 로드 후 카운트
$posts = Post::with('comments')->get();
foreach ($posts as $post) {
    echo $post->comments->count(); // 메모리 낭비
}

// ✅ 데이터베이스에서 카운트
$posts = Post::withCount('comments')->get();
foreach ($posts as $post) {
    echo $post->comments_count; // 효율적
}

// ============================================
// 청크 처리 (대량 데이터)
// ============================================

// ❌ 메모리 초과 가능
$posts = Post::all();
foreach ($posts as $post) {
    // 처리
}

// ✅ 청크로 나눠서 처리
Post::chunk(200, function ($posts) {
    foreach ($posts as $post) {
        // 처리
    }
});

// ✅ chunkById (더 안전)
Post::chunkById(200, function ($posts) {
    foreach ($posts as $post) {
        $post->update(['processed' => true]);
    }
});

// ============================================
// 캐싱
// ============================================

use Illuminate\Support\Facades\Cache;

// 쿼리 결과 캐싱
$posts = Cache::remember('posts.published', 3600, function () {
    return Post::published()
        ->with('author', 'tags')
        ->latest('published_at')
        ->take(10)
        ->get();
});

// 태그 기반 캐싱
$post = Cache::tags(['posts'])->remember("post:{$id}", 3600, function () use ($id) {
    return Post::with('author', 'comments', 'tags')->findOrFail($id);
});

// 캐시 무효화
Cache::tags(['posts'])->flush();
```

### 4. 트랜잭션

```php
<?php

use Illuminate\Support\Facades\DB;

// ============================================
// 기본 트랜잭션
// ============================================

DB::transaction(function () {
    $user = User::create([
        'name' => 'John',
        'email' => 'john@example.com',
    ]);

    $user->posts()->create([
        'title' => 'First Post',
        'content' => 'Content...',
    ]);
});

// ============================================
// 수동 트랜잭션
// ============================================

DB::beginTransaction();

try {
    $user = User::create([...]);
    $post = $user->posts()->create([...]);

    DB::commit();
} catch (\Exception $e) {
    DB::rollBack();
    throw $e;
}

// ============================================
// 트랜잭션과 함께 사용하는 서비스
// ============================================

class OrderService
{
    public function createOrder(User $user, array $items): Order
    {
        return DB::transaction(function () use ($user, $items) {
            // 주문 생성
            $order = $user->orders()->create([
                'total' => 0,
                'status' => 'pending',
            ]);

            $total = 0;

            // 주문 항목 생성 및 재고 감소
            foreach ($items as $item) {
                $product = Product::lockForUpdate()->find($item['product_id']);

                if ($product->stock < $item['quantity']) {
                    throw new \Exception("Insufficient stock for {$product->name}");
                }

                $order->items()->create([
                    'product_id' => $product->id,
                    'quantity' => $item['quantity'],
                    'price' => $product->price,
                ]);

                $product->decrement('stock', $item['quantity']);
                $total += $product->price * $item['quantity'];
            }

            // 총액 업데이트
            $order->update(['total' => $total]);

            return $order;
        });
    }
}
```

## 테스트

```php
<?php

namespace Tests\Feature;

use App\Models\Post;
use App\Models\User;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;

class PostTest extends TestCase
{
    use RefreshDatabase;

    public function test_user_can_create_post()
    {
        $user = User::factory()->create();

        $post = $user->posts()->create([
            'title' => 'Test Post',
            'content' => 'Test content',
            'status' => 'draft',
        ]);

        $this->assertDatabaseHas('posts', [
            'title' => 'Test Post',
            'user_id' => $user->id,
        ]);

        $this->assertEquals('test-post', $post->slug);
    }

    public function test_post_can_be_published()
    {
        $post = Post::factory()->create(['status' => 'draft']);

        $post->publish();

        $this->assertEquals('published', $post->status);
        $this->assertNotNull($post->published_at);
    }

    public function test_published_scope_returns_only_published_posts()
    {
        Post::factory()->count(3)->create(['status' => 'published']);
        Post::factory()->count(2)->create(['status' => 'draft']);

        $publishedPosts = Post::published()->get();

        $this->assertCount(3, $publishedPosts);
    }

    public function test_post_has_author()
    {
        $user = User::factory()->create();
        $post = Post::factory()->create(['user_id' => $user->id]);

        $this->assertInstanceOf(User::class, $post->author);
        $this->assertEquals($user->id, $post->author->id);
    }

    public function test_eager_loading_reduces_queries()
    {
        User::factory()
            ->has(Post::factory()->count(3))
            ->count(5)
            ->create();

        // N+1 확인
        \DB::enableQueryLog();

        $posts = Post::with('author')->get();
        foreach ($posts as $post) {
            $post->author->name;
        }

        $queries = \DB::getQueryLog();
        $this->assertCount(2, $queries); // posts + users (Eager Loading)
    }
}
```

## 모범 사례 요약

### ✅ DO (권장)

1. **Fat Models, Skinny Controllers**
   - 비즈니스 로직은 Model에
   - Controller는 최소한의 코디네이션만

2. **스코프 활용**
   - 재사용 가능한 쿼리는 스코프로

3. **Eager Loading**
   - N+1 문제 방지

4. **트랜잭션 사용**
   - 여러 작업을 원자적으로

5. **이벤트/옵저버**
   - 부수 효과는 분리

### ❌ DON'T (지양)

1. **God Object 방지**
   - 너무 많은 책임을 가진 모델

2. **Raw SQL 남용**
   - Eloquent의 장점 활용

3. **Lazy Loading 남용**
   - 항상 Eager Loading 고려

4. **Mass Assignment 취약점**
   - $fillable/$guarded 설정

5. **비즈니스 로직을 Controller에**
   - Model에 위치시킬 것

---

**다음 단계**: 실전 프로젝트에 적용하고, Repository 패턴과 비교해보세요!
