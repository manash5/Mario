package physics2d.enums;

// An enum (short for enumeration) is a special data type in Java (and many other programming languages)
// that is used to define a collection of constants. These constants represent a fixed set of related values that do not change.

// enum ensures that only valid predefined constants are used. For example,
// a variable of type BodyType can only hold values like Static, Dynamic, or Kinematic

public enum BodyType {
    Static, // Does not move
    Dynamic, // Moves and interacts with other objects
    Kinematic // Moves but does not get affected by forces like gravity.
}
