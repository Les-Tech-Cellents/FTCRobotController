package org.firstinspires.ftc.teamcode.control;

import java.util.HashMap;

public interface Mechanism {
    void move();

    HashMap<String, Object> getData();

    void setData(HashMap<String, String> data);

}
