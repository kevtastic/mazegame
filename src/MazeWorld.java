//Assignment 9
//Kevin Altschuler
//kaltschu
//Andrew Barrett
//andrew
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;

import tester.*;
import javalib.colors.*;
import javalib.worldimages.*;
import javalib.impworld.*;

import java.awt.Color;

//Represents an Edge of a Cell
class Edge implements Comparator<Edge>, Comparable<Edge> {
    Integer weight;
    Cell c1;
    Cell c2;
    Edge(Cell c1, Cell c2)
    {
        this.c1 = c1;
        this.c2 = c2;
        Random r = new Random();
        weight = r.nextInt(100);
    }
    Edge(Integer weight, Cell c1, Cell c2) {
        this.weight = weight;
        this.c1 = c1;
        this.c2 = c2;
    }
    //compares two edges
    public int compare(Edge e1, Edge e2) {
        return e1.weight - e2.weight;
    }
    //compares this edge to another
    public int compareTo(Edge e) {
        return this.weight.compareTo(e.weight);
    }
    @Override public boolean equals(Object that) {
        if (!(that instanceof Edge)) {
            return false;
        }
        else {
            Edge e = (Edge) that;
            return ((this.c1.equals(e.c1) && this.c2.equals(e.c2)) ||
                    this.c1.equals(e.c2) && this.c2.equals(e.c1));
        }

    }
    //Draws Edge Based on Position
    WorldImage edgeImage() {
        if (this.c1.x == this.c2.x) {
            return new LineImage(new Posn((this.c1.x * Cell.SIZE),
                    this.c1.y * Cell.SIZE + Cell.SIZE),
                    new Posn(this.c1.x * Cell.SIZE + Cell.SIZE,
                            this.c1.y * Cell.SIZE + Cell.SIZE), new Black());
        }
        else
        {
            return new LineImage(new Posn(this.c1.x * Cell.SIZE + Cell.SIZE, 
                    this.c1.y * Cell.SIZE),
                    new Posn(this.c1.x * Cell.SIZE + Cell.SIZE, 
                            this.c1.y * Cell.SIZE + Cell.SIZE), new Black());
        }
    }
}
// Represents a single square of the game area
class Cell {
    // In logical coordinates, with the origin at the top-
    // left corner of the screen
    int x;
    int y;
    ArrayList<Edge> neighbors;
    //size in pixels
    static final int SIZE = 10;
    Cell(int x, int y) {
        this.x = x;
        this.y = y;
        this.neighbors = new ArrayList<Edge>();
    }
  //Checks equality between Cell
    @Override public boolean equals(Object that) {
        if (!(that instanceof Cell)) {
            return false;
        }
        else {
            Cell c = (Cell) that;
            return (this.x == c.x && this.y == c.y);
        }
    }
  //Checks the Hash Code of a Cell
    @Override public int hashCode() {
        return this.x + this.y;
    }
    //Finds if this cell is attached to hashmap h
    Cell find(HashMap<Cell, Cell> h) {
        if (this.equals(h.get(this))) {
            return this;
        }
        else {
            return h.get(this).find(h);
        }
    }
    //makes all possible neighboring cells of this
    public void makeNeighbors(ArrayList<Edge> edges) {
        for (Edge e : edges) {
            if (e.c1.equals(this)) {
                this.neighbors.add(e);
            }
            if (e.c2.equals(this)) {
                Edge temp = new Edge(e.weight, e.c2, e.c1);
                this.neighbors.add(temp);
            }
        }
    }
}
//To Represent a Player
class Player {
    Cell cell;
    Player(Cell cell) {
        this.cell = cell;
    }
}
//To Represent a Stack
class Stack<T> {
    ArrayList<T> list;
    Stack(ArrayList<T> list) {
        this.list = list;
    }
    //Adds to the end of the Stack
    public void add(T t) {
        list.add(0, t);
    }
    //Removes from the Stack
    public T pop() { 
        T result = list.get(0);
        list.remove(0);
        return result;
    }
}
//To Represent a Queue
class Queue<T> {
    ArrayList<T> list;
    Queue(ArrayList<T> list) {
        this.list = list;
    }
    //Adds to the end of the Queue
    public void add(T t) {
        list.add(list.size(), t);
    }
    //Removes from the Queue
    public T pop() { 
        T result = list.get(0);
        list.remove(0);
        return result;
    }
}
//To Represent a MazeWorld
class MazeWorld extends World {
    static final int WIDTH = 64;
    static final int HEIGHT = 60;
    boolean depth;
    boolean breadth;
    //player
    Player player = new Player(new Cell(0, 0));
    // all the cells
    ArrayList<ArrayList<Cell>> board = new ArrayList<ArrayList<Cell>>();
    ArrayList<Cell> visited = new ArrayList<Cell>();
    ArrayList<Edge> edges = new ArrayList<Edge>();
    ArrayList<Edge> walls = new ArrayList<Edge>();
    HashMap<Cell, Cell> representatives = new HashMap<Cell, Cell>();
    Stack<Edge> dWorklist = new Stack<Edge>(new ArrayList<Edge>());
    Queue<Edge> bWorklist = new Queue<Edge>(new ArrayList<Edge>());
    MazeWorld() {
        //default constructor
        this.reset(MazeWorld.WIDTH * Cell.SIZE, MazeWorld.HEIGHT * Cell.SIZE);
    }
    //Creates a new Random Maze
    void reset(int width, int height) {
        player = new Player(new Cell(0, 0));
        // all the cells
        board = new ArrayList<ArrayList<Cell>>();
        visited = new ArrayList<Cell>();
        edges = new ArrayList<Edge>();
        walls = new ArrayList<Edge>();
        representatives = new HashMap<Cell, Cell>();
        dWorklist = new Stack<Edge>(new ArrayList<Edge>());
        bWorklist = new Queue<Edge>(new ArrayList<Edge>());
        breadth = false;
        depth = false;
        for (int i = 0; i <= MazeWorld.WIDTH; i += 1) {
            ArrayList<Cell> row = new ArrayList<Cell>();
            for (int j = 0; j <= MazeWorld.HEIGHT; j += 1) {
                row.add(new Cell(i, j));
            }
            board.add(row);
        }
        for (int i = 0; i <= MazeWorld.WIDTH; i += 1) {
            for (int j = 0; j <= MazeWorld.HEIGHT; j += 1) {
                if (board.get(i).get(j).x != MazeWorld.WIDTH)
                {
                    edges.add(new Edge(board.get(i).get(j), board.get(i + 1).get(j)));
                }
                if (board.get(i).get(j).y != MazeWorld.HEIGHT)
                {
                    edges.add(new Edge(board.get(i).get(j), board.get(i).get((j + 1))));
                }
            }
        }
        Collections.sort(edges);
        for (int i = 0; i <= MazeWorld.WIDTH; i += 1) {
            for (int j = 0; j <= MazeWorld.HEIGHT; j += 1) {
                representatives.put(board.get(i).get(j), board.get(i).get(j));
            }
        }
        this.kruskals();
        for (ArrayList<Cell> row : board) {
            for (Cell c : row) {
                c.makeNeighbors(this.edges);
            }
        }
        player.cell = board.get(0).get(0);
    }
    //Run's Kruskal's Algorithm when constructing a maze
    void kruskals() {
        ArrayList<Edge> workList = new ArrayList<Edge>();
        workList.addAll(edges);
        edges.clear();
        while (workList.size() > 1)
        {
            Edge e1 = workList.get(0);
            if (e1.c1.find(representatives).equals(e1.c2.find(representatives))) {
                workList.remove(0);
                this.walls.add(e1);
            }
            else {
                this.edges.add(e1);
                union(representatives, e1.c1.find(representatives), e1.c2.find(representatives));
                workList.remove(0);
            }
        }
    }
    //Unions two cells that share an Edge
    void union(HashMap<Cell, Cell> h, Cell c1, Cell c2) {
        h.put(c1, c2);
    }
    // overlay background onto cells
    public WorldImage makeImage() {
        WorldImage acc = new RectangleImage(new Posn(0, 0), 
                0, 0, new Black());
        for (Edge e: walls)
        {
            acc = new OverlayImages(acc, e.edgeImage());
        }
        for (Cell c : visited) {
            acc = new OverlayImages(acc, new RectangleImage(
                    new Posn(c.x * Cell.SIZE + Cell.SIZE / 2,
                            c.y * Cell.SIZE + Cell.SIZE / 2), 
                            Cell.SIZE - 1, Cell.SIZE - 1, new Color(157, 225, 250)));
        }
        acc = new OverlayImages(acc, new RectangleImage(
                new Posn(this.player.cell.x * Cell.SIZE + Cell.SIZE / 2,
                        this.player.cell.y * Cell.SIZE + Cell.SIZE / 2), 
                        Cell.SIZE - 1, Cell.SIZE - 1, new Blue()));
        return acc;
    }
    //Runs Depth First
    void updateDepth() {
        if (dWorklist.list.size() > 0) {
            Edge next = dWorklist.pop();
            this.visited.add(next.c1);
            this.player.cell = next.c2;
            if (next.c2.equals(new Cell(MazeWorld.WIDTH, MazeWorld.HEIGHT))) {
                this.depth = false;
            }
            else if (!this.visited.contains(next.c2) && 
                    !next.c2.equals(this.board.get(0).get(0))) {
                for (Edge e : player.cell.neighbors) {
                    dWorklist.add(e);
                }
            }
        }
    }
    //Runs Breadth First
    void updateBreadth() {
        if (bWorklist.list.size() > 0) {
            Edge next = bWorklist.pop();
            this.visited.add(next.c1);
            this.player.cell = next.c2;
            if (next.c2.equals(new Cell(MazeWorld.WIDTH, MazeWorld.HEIGHT))) {
                this.breadth = false;
            }
            else if (!this.visited.contains(next.c2) && 
                    !next.c2.equals(this.board.get(0).get(0))) {
                for (Edge e : player.cell.neighbors) {
                    bWorklist.add(e);
                }
            }
        }
    }
    // handle key events
    public void onKeyEvent(String ke) {
        updatePlayer(ke);
    }
    //Moves the player with arrow keys, or either search method
    public void updatePlayer(String ke) {
        if (ke.equals("d")) {
            if (this.player.cell.equals(board.get(0).get(0))) {
                dWorklist = new Stack<Edge>(player.cell.neighbors);
            }
            this.depth = !this.depth;
        }
        if (ke.equals("b")) {
            if (this.player.cell.equals(board.get(0).get(0))) {
                bWorklist = new Queue<Edge>(player.cell.neighbors);
            }
            this.breadth = !this.breadth;
        }
        if (ke.equals("r")) {
            this.reset(MazeWorld.WIDTH * Cell.SIZE, MazeWorld.HEIGHT * Cell.SIZE);
        }
        if (ke.equals("up") && this.containsEdge(new Edge(player.cell, 
                new Cell(player.cell.x, player.cell.y - 1)))) {
            player.cell = new Cell(player.cell.x, player.cell.y - 1);
            visited.add(player.cell);
        }
        if (ke.equals("down") && this.containsEdge(new Edge(player.cell, 
                new Cell(player.cell.x, player.cell.y + 1)))) {
            player.cell = new Cell(player.cell.x, player.cell.y + 1);
            visited.add(player.cell);
        }
        if (ke.equals("left") && this.containsEdge(new Edge(this.player.cell, 
                new Cell(this.player.cell.x - 1, this.player.cell.y)))) {
            this.player.cell = new Cell(this.player.cell.x - 1, this.player.cell.y);
            this.visited.add(this.player.cell);
        }
        if (ke.equals("right") && this.containsEdge(new Edge(this.player.cell, 
                new Cell(this.player.cell.x + 1, this.player.cell.y)))) {
            this.player.cell = new Cell(this.player.cell.x + 1, this.player.cell.y);
            this.visited.add(this.player.cell);
        }
    }
    // checks if that edge is in this maze
    public boolean containsEdge(Edge that) {
        boolean result = false;
        for (Edge e : edges) {
            if (e.equals(that)) {
                result = true;
            }
        }
        return result;
    }
    //update the player's position
    // method for each tick
    public void onTick() {
        if (this.depth) {
            this.updateDepth();
        }
        if (this.breadth) {
            this.updateBreadth();
        }
    }
}
//Tests and Examples for MazeWorld
class ExamplesWorld {
    Cell c1 = new Cell(1, 1);
    Cell c2 = new Cell(1, 1);
    Cell c3 = new Cell(1, 2);
    Edge e1 = new Edge(1, c1, c3);
    Edge e2 = new Edge(2, c3, c1);
    Edge e4 = new Edge(3, c2, c1);
    boolean testSameCell(Tester t) {
        return t.checkExpect(c1.equals(c2), true) &&
                t.checkExpect(c1.equals(c3), false);
    }
    boolean testSameEdge(Tester t) {
        return t.checkExpect(e1.equals(e1), true) &&
                t.checkExpect(e4.equals(e1), false);
    }
    boolean testCompare(Tester t) {
        return t.checkExpect(e1.compare(e2, e1), 1);
    }
    boolean testCompareto(Tester t) {
        return t.checkExpect(e1.compareTo(e2), -1);
    }
    boolean testEdgeImage(Tester t) {
        return t.checkExpect(e1.edgeImage(), new LineImage(new Posn((this.c1.x * Cell.SIZE),
                this.c1.y * Cell.SIZE + Cell.SIZE),
                new Posn(this.c1.x * Cell.SIZE + Cell.SIZE,
                        this.c1.y * Cell.SIZE + Cell.SIZE), new Black()));
    }
    boolean testHashCode(Tester t) {
        return t.checkExpect(c1.hashCode(), 2);
    }
    int runAnimation() {
        MazeWorld m1 = new MazeWorld();
        m1.bigBang(Cell.SIZE * MazeWorld.WIDTH + 10, Cell.SIZE * MazeWorld.HEIGHT + 10, .001);
        return 1;
    }
    int run = this.runAnimation();
}