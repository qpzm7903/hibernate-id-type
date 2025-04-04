# Changelog

## 2025-03-31: 重构IdentifierType和配置加载机制

### 改进
- 将配置加载功能从IdentifierType类中抽取到独立的ConfigurationLoader工具类
- 简化了IdentifierType类，使其更专注于类型转换功能
- 使用ConfigurationLoader工具类统一处理配置加载逻辑

### 原因
- 遵循单一职责原则，分离配置加载和类型转换职责
- 提高代码的可读性和可维护性
- 便于在其他组件中复用配置加载功能

### 影响
- 所有测试继续通过，确保功能完整性
- 代码结构更清晰，职责分明
- 提高了系统的模块化程度

## 2025-03-31: 移除不必要的Spring上下文依赖

### 变更

- 移除了`IdentifierType`类中的`ApplicationContextAware`和`BeanFactoryAware`接口实现
- 移除了对Spring应用上下文和Bean工厂的静态引用
- 简化了依赖解析逻辑，专注于直接从配置文件加载配置

### 原因

- 由于已经实现了从配置文件直接加载配置的功能，不再需要通过Spring上下文获取配置
- 减少了不必要的依赖，使代码更加简洁和独立
- 简化了类的设计，提高了可维护性

### 影响

- 所有测试继续通过，确保功能完整性
- 代码更加精简，更易于理解和维护

## 2025-03-31: 增强IdentifierType的配置加载能力

### 变更

- 增强了`IdentifierType`类，使其能够从不同的配置源直接加载配置，包括：
  - 类路径下的application.properties
  - 文件系统中的application.properties
  - 支持在各种不同的部署场景下正常工作（如java -jar, java -cp等）

### 原因

- 解决在某些部署环境下无法通过Spring容器访问配置的问题
- 确保`IdentifierType`在Hibernate直接实例化时能够正常工作
- 提高代码的健壮性，使其在各种环境下都能可靠运行

### 影响

- 所有测试用例继续通过
- 提高了应用程序在各种部署环境下的可靠性

## 2025-03-31: Fix Hibernate Direct Instantiation Issue

### Summary
Fixed a critical issue where Hibernate was directly instantiating the `IdentifierType` class without using Spring's dependency injection, causing `NullPointerException` due to uninitialized dependencies.

### Changes
- Made `IdentifierType` implement `ApplicationContextAware` to access the Spring application context
- Added a no-args constructor required by Hibernate for direct instantiation
- Implemented lazy initialization for dependencies (`DatabaseTypeResolver` and `IdentifierProperties`)
- Added fallback default implementations when the application context is not available
- Converted direct field accesses to use getter methods with lazy initialization

### Rationale
Hibernate instantiates custom `UserType` implementations directly using reflection, bypassing Spring's dependency injection system. This causes `NullPointerException` when the injected dependencies are accessed. The solution provides a way for the `IdentifierType` class to access its dependencies even when instantiated directly by Hibernate.

### Testing
All tests are now passing with the updated implementation.

## 2025-03-31: Remove Unused Type Prefix Constants

### Summary
Removed the `LONG_TYPE_PREFIX` and `STRING_TYPE_PREFIX` constants from the `IdentifierType` class along with their related logic. These prefixes were being used for non-native type handling, which is no longer needed as the system now uses native database types.

### Changes
- Removed `LONG_TYPE_PREFIX` and `STRING_TYPE_PREFIX` constants
- Simplified `nullSafeGet` logic to directly parse values without prefix checking
- Updated `nullSafeSet` to use direct string representation without prefixes
- Simplified `disassemble` and `assemble` methods to use the string representation directly

### Rationale
According to the PRD, the system now uses native database types for ID storage:
- When configured for Long ID type, it uses BIGINT columns
- When configured for String ID type, it uses VARCHAR columns

The prefix-based approach for storing type information was only needed when both types might be stored in the same column type, which is no longer the case. This change simplifies the code and reduces the risk of serialization/deserialization issues.

### Testing
All tests continue to pass after these changes, confirming that the removed logic was indeed unused. 