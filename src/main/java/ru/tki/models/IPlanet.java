package ru.tki.models;


public interface IPlanet {
    Coordinates getCoordinates();

    String getName();

    PlanetType getType();
}
