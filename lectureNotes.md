# NOTES
## Inheritance
Can add a child class with keyword "extends"
`super()` is used to call the parent constructor
The constructor is named after the class name
Can inherit parameters but not constructor
Have to be careful when changing the parent
Single level / Multi level / Multiple inheritance (not supported in java because if the 2+ parent classes have the same method, child class won't know which to use)
Can achieve multiple inheritance by using an Interface
## Abstraction
Interfaces have only abstract methods and they have to be only public
They can contain only public methods


abstract Class - partial abstraction
interface Class - 100% abstraction
abstraction is the process of hiding the implementation detail
when we send a message, we do not know the process of sending the message, but we only know what it does and doesnt know how it does it
abstract methods cannot be final or static


an abstract method can not be included in a normal class, but only in an abstract class

java allows implementing multipe interfaces with keyword "implements"

## Polymorphysm
poly - multiple
morph - form
Can be achieved by inheritence and method overriding
Compile-time polymorphysm (early binding - upcasting)
Runtime polymorphysm (late binding): JVM decides which method to call
Reference variable of a parent class that points to a child class 
Parent class cannot use the methods of child class
The logic which method to call if The type of the object was referenced by a superclass or subclass

super keyword - can call the parent constructor and methods
this keyword - refers to the current class

static initialization block (always executed first)
instance initialization block

final keyword
 - similar to constant
 - final classes cannot be inherited
 - cannot overrife final methods

## LINKED LIST
### Generic types
advantages - type safety, reusable
#### Casting
- imlicit casting (int to double - no data loss because smaller data type -> bigger data type)
- explicit casting (double to int - data loss because bigger data type -> smaller data type)


## Autoboxing & Unboxing in Java?

HASHMAP
## ENUM
Contains a fixed set of constants, ex. days of the week
Is both a data type and a class

## Java environment variables
### Tools
-Jasypt

## Data structures
-Array List (dynamic) costs resources
When the memory runs out to store values, it finds a free space in the memory, creates a larger array list, moves the values there and garbage collector deletes the old list.
-Linked List
-HashMap

## Coding principles

DRY (Don’t Repeat Yourself)
Avoid code duplication by reusing logic and components.

SOLID Principles
S - Single Responsibility (SRP)
Each class has one job only.

O - Open/Closed (OCP)
Open for extension, closed for modification.

L - Liskov Substitution (LSP)
Subclasses can replace parent classes without breaking the app.

I - Interface Segregation (ISP)
Use smaller, specific interfaces instead of one large one.
- split concerns in an interface
- multiple abstract methods assigned to a class should be separated by interfaces


D - Dependency Inversion (DIP)
Depend on abstractions, not concrete implementations.

## File processing
### CSV JSON XML

## Functional interfaces**
- Interface that contains only one abstract method
- Predicate: always boolean as return type
- Function: requires input, returns output
- Suplier: doesnt require input, returns output
- Consumer: requires input, doesnt return anything
## Lambda expressions**
- used with functional interfaces to implement the abstract method 

## Streams 
- a set of operations that are applied to a source of data
- (list, file, database)
- Stream Interface, Intermediate Operations, Terminal Operations, Collectors
- Does not affect the source data. Need a new structure to use output (collectors)
## Method references