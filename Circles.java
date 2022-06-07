import tester.*;                // The tester library
import javalib.worldimages.*;   // images, like RectangleImage or OverlayImages
import javalib.funworld.*;      // the abstract World class and the big-bang library
import java.awt.Color;          // general colors (as triples of red,green,blue values)
                                // and predefined colors (Color.RED, Color.GRAY, etc.)
/*
//to represent a position
class MyPosn extends Posn { 
  // standard constructor
  MyPosn(int x, int y) {
    super(x, y);
  }
 
  // constructor to convert from a Posn to a MyPosn
  MyPosn(Posn p) {
    this(p.x, p.y);
  }
  
  //given a MyPosn adds to this MyPosn
  MyPosn add(MyPosn p) {
    return new MyPosn(this.x + p.x, this.y + p.y);
  }
  
  MyPosn scale(int x) {
    return new MyPosn(this.x * x, this.y *y);
  }
  
  //determines if this MyPosn is off the screen
  boolean isOffScreen(int width, int height) {
    return this.x < 0 || this.x > width || this.y < 0 || this.y > height;
  }
}
*/

//to represent a circle
class Circle {
  MyPosn position; // in pixels
  MyPosn velocity; // in pixels/tick
  int radius;
  Color color;
  
  //constructor
  Circle(MyPosn position, MyPosn velocity, int radius, Color color) {
    this.position = position;
    this.velocity = velocity;
    this.radius = radius;
    this.color = color;
  }
  
  //moves this circle to its new position after one tick
  Circle move() {
    return new Circle (this.position.add(this.velocity), this.velocity, this.radius, this.color);
  }
  
  //determines if this circle is off screen given a width and a height
  boolean isOffScreen(int width, int height) {
    return this.position.isOffScreen(width, height);
  }
  
  //draws this circle
  WorldImage draw() {
    return new CircleImage(this.radius, OutlineMode.SOLID, this.color);
  }
  
  //places this circle in its appropriate position on the screen
  WorldScene place(WorldScene scene) {
    return scene.placeImageXY(this.draw(), this.position.x, this.position.y);
  }
  
  //determines if this circle overlaps with the other circle
  boolean overlap(Circle other) {
    return ((this.position.x - other.position.x) * (this.position.x - other.position.x) +
          (this.position.y - other.position.y) * (this.position.y - other.position.y)) <=
          (this.radius + other.radius) * (this.radius + other.radius);
  }
}

//to represent a list of circles
interface ILoCircle {
  //moves all the circles in this list of circles
  ILoCircle moveAll();
  //removes all the circles in this list of circles that are off screen
  ILoCircle removeOffScreen(int width, int height);
  //places all the circles in this list of circles
  WorldScene placeAll(WorldScene s); 
}

//to represent an empty list of circles
class MtLoCircle implements ILoCircle {
  //moves all the circles in this empty list of circles
  public ILoCircle moveAll() {
    return this;
  }
  //removes all the circles in this empty list of circles that are off screen
  public ILoCircle removeOffScreen(int width, int height) {
    return this;
  }
  
  //places all the circles in this empty list of circles
  public WorldScene placeAll(WorldScene scene) {
    return scene;
  }
}

//to represent a nonempty list of circles 
class ConsLoCircle implements ILoCircle {
  Circle first;
  ILoCircle rest;
  
  //the constructor
  ConsLoCircle(Circle first, ILoCircle rest) {
    this.first = first;
    this.rest = rest;
  }
  
  //moves all the circles in this nonempty list of circles
  public ILoCircle moveAll() {
    return new ConsLoCircle(this.first.move(), this.rest.moveAll());
  }
  
  //removes all the circles in this nonempty list of circles that are off screen
  public ILoCircle removeOffScreen(int width, int height) {
    if (this.first.isOffScreen(width, height)) {
      return this.rest.removeOffScreen(width, height);
    }
    else {
      return new ConsLoCircle(this.first, this.rest.removeOffScreen(width, height));
    }
  }
  
  //places all the circles in this nonempty list of circles
  public WorldScene placeAll(WorldScene scene) {
    return this.rest.placeAll(this.first.place(scene));
  }
}
