# CLAUDE.md - AI Assistant Guide for study-architecture

**Last Updated**: 2025-12-18
**Repository**: study-architecture
**Purpose**: A repository for studying and implementing software architecture patterns and principles

---

## Repository Overview

This repository is designed for learning and experimenting with software architecture patterns, design principles, and best practices. It serves as a practical workspace for implementing various architectural approaches and studying their trade-offs.

### Current State
- **Status**: Initial setup
- **Structure**: Minimal (README.md only)
- **Language**: Not yet determined (Python, JavaScript, Java, Go, or polyglot approach)
- **Dependencies**: None yet

---

## Repository Structure

```
study-architecture/
├── README.md           # Project overview and introduction
└── CLAUDE.md          # This file - AI assistant guide
```

### Expected Future Structure

As the repository grows, expect to see:

```
study-architecture/
├── docs/              # Documentation for patterns and principles
├── examples/          # Practical implementations of patterns
│   ├── creational/    # Creational design patterns
│   ├── structural/    # Structural design patterns
│   └── behavioral/    # Behavioral design patterns
├── architecture/      # Larger architectural patterns
│   ├── layered/       # Layered architecture examples
│   ├── microservices/ # Microservices patterns
│   ├── event-driven/  # Event-driven architecture
│   └── hexagonal/     # Hexagonal/Clean architecture
├── tests/             # Unit and integration tests
└── resources/         # Additional learning resources
```

---

## Development Workflow

### Branching Strategy
- **Main branch**: `main` (or `master`)
- **Feature branches**: Use descriptive names like `feature/observer-pattern` or `arch/microservices-example`
- **Claude branches**: Auto-created with `claude/` prefix for AI-assisted development

### Commit Conventions
- Use clear, descriptive commit messages
- Follow conventional commits format when possible:
  - `feat:` for new features/patterns
  - `docs:` for documentation updates
  - `refactor:` for code improvements
  - `test:` for test additions
  - `fix:` for bug fixes

Example:
```
feat: implement observer pattern example
docs: add state pattern documentation
refactor: improve factory pattern implementation
```

---

## Code Conventions

### General Principles
1. **Clarity over cleverness**: Code should be readable and self-documenting
2. **SOLID principles**: Follow Single Responsibility, Open/Closed, Liskov Substitution, Interface Segregation, and Dependency Inversion
3. **DRY (Don't Repeat Yourself)**: Avoid code duplication
4. **KISS (Keep It Simple, Stupid)**: Prefer simple solutions
5. **Documentation**: Each pattern/example should include:
   - Purpose and use cases
   - Implementation details
   - Trade-offs and considerations
   - Example usage

### File Organization
- One pattern per directory
- Include README.md in each pattern directory explaining:
  - What the pattern is
  - When to use it
  - Pros and cons
  - Implementation notes

### Naming Conventions
- **Files**: Use lowercase with hyphens (e.g., `observer-pattern.py`)
- **Classes**: Use PascalCase (e.g., `ObserverPattern`)
- **Functions/Methods**: Use camelCase or snake_case depending on language
- **Constants**: Use UPPER_SNAKE_CASE

---

## Testing Standards

### Test Coverage
- Aim for high test coverage (80%+ for business logic)
- Test both typical and edge cases
- Include integration tests for architectural patterns

### Test Organization
- Mirror source structure in tests directory
- Name test files with `test_` prefix or `_test` suffix
- Group related tests in test classes/suites

---

## Documentation Requirements

### Pattern Documentation Template

Each architectural pattern or design pattern should include:

```markdown
# [Pattern Name]

## Overview
Brief description of the pattern

## Intent
What problem does this pattern solve?

## Applicability
When should you use this pattern?

## Structure
Diagram or description of the pattern's structure

## Participants
Key components and their roles

## Collaborations
How components interact

## Implementation
Code examples and explanations

## Consequences
Pros and cons, trade-offs

## Related Patterns
Similar or complementary patterns

## References
Books, articles, or resources
```

---

## AI Assistant Guidelines

### When Adding New Patterns

1. **Research First**: Read existing code to understand the structure
2. **Follow Templates**: Use the pattern documentation template
3. **Add Examples**: Include practical, runnable examples
4. **Test Thoroughly**: Write tests before marking work complete
5. **Document Trade-offs**: Explain when to use vs. not use the pattern

### When Refactoring

1. **Preserve Intent**: Keep the educational value intact
2. **Improve Clarity**: Make code more understandable
3. **Update Docs**: Reflect changes in documentation
4. **Test Coverage**: Ensure tests still pass

### When Asked Questions

1. **Understand Context**: Read related files before answering
2. **Cite Sources**: Reference specific files and line numbers
3. **Explain Trade-offs**: Discuss pros and cons
4. **Suggest Alternatives**: Mention related patterns or approaches

### Code Quality Checklist

Before completing work:
- [ ] Code follows language-specific conventions
- [ ] Tests are written and passing
- [ ] Documentation is updated
- [ ] Examples are runnable and clear
- [ ] Trade-offs are documented
- [ ] No security vulnerabilities introduced
- [ ] No unnecessary complexity added

---

## Common Architecture Patterns to Study

### Design Patterns (Gang of Four)

**Creational**
- Singleton
- Factory Method
- Abstract Factory
- Builder
- Prototype

**Structural**
- Adapter
- Bridge
- Composite
- Decorator
- Facade
- Flyweight
- Proxy

**Behavioral**
- Chain of Responsibility
- Command
- Interpreter
- Iterator
- Mediator
- Memento
- Observer
- State
- Strategy
- Template Method
- Visitor

### Architectural Patterns

- **Layered Architecture**: Separation into layers (presentation, business, data)
- **Microservices**: Distributed, independently deployable services
- **Event-Driven**: Communication through events
- **Hexagonal/Clean**: Dependency inversion, ports and adapters
- **CQRS**: Command Query Responsibility Segregation
- **Event Sourcing**: State changes as sequence of events
- **Service-Oriented**: Services communicating via protocols
- **Serverless**: Function-as-a-Service architectures
- **MVC/MVVM**: Model-View-Controller/ViewModel patterns

---

## Technology Considerations

### Language Selection
- **Python**: Great for quick prototypes, clear syntax
- **Java**: Excellent for classic OOP patterns
- **TypeScript/JavaScript**: Modern web architectures
- **Go**: Microservices and concurrent patterns
- **Rust**: Memory safety and systems patterns

### Tools and Frameworks
- Testing frameworks appropriate to chosen language
- Diagram tools (PlantUML, Mermaid) for architecture diagrams
- Documentation generators (Sphinx, JSDoc, etc.)

---

## Resources and References

### Books
- "Design Patterns: Elements of Reusable Object-Oriented Software" (Gang of Four)
- "Clean Architecture" by Robert C. Martin
- "Domain-Driven Design" by Eric Evans
- "Patterns of Enterprise Application Architecture" by Martin Fowler
- "Building Microservices" by Sam Newman

### Online Resources
- refactoring.guru - Design patterns catalog
- martinfowler.com - Architecture and patterns articles
- Microsoft Architecture Guides
- AWS Architecture Center

---

## Git Workflow for AI Assistants

### Before Starting Work
1. Check current branch status
2. Understand the requested changes
3. Read relevant existing files
4. Plan the implementation

### During Development
1. Make focused, atomic commits
2. Write clear commit messages
3. Update documentation alongside code
4. Run tests frequently

### Before Pushing
1. Verify all tests pass
2. Check documentation is complete
3. Review changes for quality
4. Ensure no sensitive data is committed

### Push Protocol
- Always push to the assigned `claude/` branch
- Use: `git push -u origin <branch-name>`
- Retry on network failures with exponential backoff

---

## Security Considerations

- **No Secrets**: Never commit API keys, passwords, or credentials
- **Input Validation**: Validate all external inputs
- **Dependency Security**: Keep dependencies updated
- **Code Review**: Review patterns for common vulnerabilities
- **OWASP Awareness**: Be aware of common security issues

---

## Future Enhancements

Potential areas for repository growth:
- Interactive tutorials
- Performance comparisons of patterns
- Real-world case studies
- Video/animation explanations
- Language-specific optimizations
- Distributed systems patterns
- Cloud-native architectures
- Domain-driven design examples

---

## Questions or Improvements?

This is a living document. As the repository evolves, this guide should be updated to reflect:
- New patterns added
- Convention changes
- Tooling updates
- Lessons learned

When in doubt, prioritize:
1. **Educational value**: Make it easy to learn
2. **Clarity**: Make it easy to understand
3. **Practicality**: Make it useful in real projects

---

**Note for AI Assistants**: Always read this file before starting work on this repository. When adding new content, ensure it aligns with the educational goals and maintains consistency with existing examples.
