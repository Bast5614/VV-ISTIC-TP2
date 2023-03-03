# TCC *vs* LCC

Explain under which circumstances *Tight Class Cohesion* (TCC) and *Loose Class Cohesion* (LCC) metrics produce the same value for a given Java class. Build an example of such as class and include the code below or find one example in an open-source project from Github and include the link to the class below. Could LCC be lower than TCC for any given class? Explain.

## Answer

Les deux permettent de calculer la cohésion d'une classe
Si on transpose dans un graph (chaque bulle = une méthode), TCC calcule le nombre de lien directe du graph, tandi que LCC calcul le nombre de lien direct et indirect d'un graph

Exemple : 

class Point {

    private double x, y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double dot(Point p) {
        return x*p.x + y*p.y;
    }

    public Point sub(Point p) {
        return new Point(x - p.x, y - p.y);
    }
}
Ici TCC=5/6 et LCC=6/6.
Non LCC ne peux pas être plus petit de TCC car ils comptes les liens directe ET indirecte. Au mieux ils sont égaux. 
