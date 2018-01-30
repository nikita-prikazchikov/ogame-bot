package ru.tki.po;

import org.openqa.selenium.WebElement;
import ru.tki.models.types.MissionType;
import ru.tki.models.types.ShipType;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractFleetDetails extends PageObject {

    static final String RESOURCE_METAL     = "Металл";
    static final String RESOURCE_CRYSTAL   = "Кристалл";
    static final String RESOURCE_DEUTERIUM = "Дейтерий";

    static final Map<ShipType, String> ships = new HashMap<ShipType, String>() {{
        put(ShipType.LIGHT_FIGHTER, "Лёгкий истребитель");
        put(ShipType.HEAVY_FIGHTER, "Тяжёлый истребитель");
        put(ShipType.CRUISER, "Крейсер");
        put(ShipType.BATTLESHIP, "Линкор");
        put(ShipType.BATTLECRUISER, "Линейный крейсер");
        put(ShipType.BOMBER, "Бомбардировщик");
        put(ShipType.DESTROYER, "Уничтожитель");
        put(ShipType.DEATHSTAR, "Звезда смерти");

        put(ShipType.SMALL_CARGO, "Малый транспорт");
        put(ShipType.LARGE_CARGO, "Большой транспорт");
        put(ShipType.COLONY_SHIP, "Колонизатор");
        put(ShipType.RECYCLER, "Переработчик");
        put(ShipType.ESPIONAGE_PROBE, "Шпионский зонд");
    }};

    private static final Map<MissionType, String> missionIds = new HashMap<MissionType, String>() {{
        put(MissionType.ATTACK, "1");
        put(MissionType.JOINT_ATTACK, "2");
        put(MissionType.TRANSPORT, "3");
        put(MissionType.KEEP, "4");
        put(MissionType.HOLD_ON, "5");
        put(MissionType.ESPIONAGE, "6");
        put(MissionType.COLONIZATION, "7");
        put(MissionType.RECYCLING, "8");
        put(MissionType.DESTROY, "9");
        put(MissionType.EXPEDITION, "15");
    }};

    protected MissionType getMissionType(WebElement element) {
        switch (element.getAttribute("data-mission-type")) {
            case "1":
                return MissionType.ATTACK;
            case "2":
                return MissionType.JOINT_ATTACK;
            case "3":
                return MissionType.TRANSPORT;
            case "4":
                return MissionType.KEEP;
            case "5":
                return MissionType.HOLD_ON;
            case "6":
                return MissionType.ESPIONAGE;
            case "7":
                return MissionType.COLONIZATION;
            case "8":
                return MissionType.RECYCLING;
            case "9":
                return MissionType.DESTROY;
            case "15":
                return MissionType.EXPEDITION;
            default:
                return MissionType.ATTACK;
        }
    }

    protected String getMissionTypeId(MissionType type) {
        return missionIds.get(type);
    }
}
