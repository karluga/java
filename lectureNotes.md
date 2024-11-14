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