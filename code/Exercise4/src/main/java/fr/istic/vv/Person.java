package fr.istic.vv;

class Person {
    private int age;
    private String name;
    private String prenom;

    public String getName() { return name; }

    public boolean isAdult() {
        return age > 17;
    }
}
