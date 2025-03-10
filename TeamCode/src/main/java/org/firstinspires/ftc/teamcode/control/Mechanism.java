package org.firstinspires.ftc.teamcode.control;

import java.util.HashMap;

public interface Mechanism {
    /**
     * Applique les mouvements précédements enregistrés
     */
    void move();

    /**
     * Permets de récupérer les données (puissances des moteurs, positions des servos...)
     * pour les enregistrer extérieurement
     * @return Une HashMap du type <code>{"nomDuChamp":valeur, ect.}</code>
     * @see #setData(HashMap)
     */
    HashMap<String, Object> getData();

    /**
     * Enregistre les données sur les variables de la classe depuis l'extérieur
     * @param data Une HashMap contenant les données du type <code>{"nomDuChamp":valeur, ect.}</code>
     * @see #getData()
     */
    void setData(HashMap<String, String> data);

}
