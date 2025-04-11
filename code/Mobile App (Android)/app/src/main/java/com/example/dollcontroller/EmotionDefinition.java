package com.example.dollcontroller;

import java.util.Arrays;

public class EmotionDefinition {

    // Set emotion list
    public static final String Emotions[] = {
        "NONE",
        "SMILE",
        "ANNOYED",
        "ANGRY",
        "LAUGH",
        "SURPRISED",
        "SAD",
        "AFRAID",
        "SHY",
        "LOVE",
        "CRY",
        "QUESTION",
        "HELLO",
        "SLEEP",
        "THINK",
        "SHOCK",
        "NORMAL DANCE (DANCE)",
        "EXERCISE (DANCE)",
        "GREETING (DANCE)",
        "RHYTHM DANCE (DANCE)"
    };

    // range 75(Left) ~ 105(Right)
    public static int Left_Ear_Angle[] = {
            90,     // NONE
            80,     // SMILE
            75,     // ANNOYED
            100,    // ANGRY
            95,     // LAUGH
            105,    // SURPRISED
            75,     // SAD
            80,     // AFRAID
            75,     // SHY
            80,     // LOVE
            75,     // CRY
            85,     // QUESTION
            90,     // HELLO
            75,     // SLEEP
            90,     // THINK
            105,    // SHOCK
            90,     // NORMAL DANCE (DANCE)
            80,     // EXERCISE (DANCE)
            90,     // GREETING (DANCE)
            80      // RHYTHM DANCE (DANCE)
    };

    // range 75(Right) ~ 105(Left)
    public static int Right_Ear_Angle[] = {
            90,     // NONE
            100,    // SMILE
            105,    // ANNOYED
            80,     // ANGRY
            85,     // LAUGH
            75,     // SURPRISED
            105,    // SAD
            100,    // AFRAID
            105,    // SHY
            100,    // LOVE
            105,    // CRY
            95,     // QUESTION
            90,     // HELLO
            105,    // SLEEP
            80,     // THINK
            75,     // SHOCK
            90,     // NORMAL DANCE (DANCE)
            80,     // EXERCISE (DANCE)
            90,     // GREETING (DANCE)
            100     // RHYTHM DANCE (DANCE)
    };

    // range 75(Right) ~ 105(Left)
    public static int Neck_Angle[] = {
            87,     // NONE
            92,     // SMILE
            92,     // ANNOYED
            87,     // ANGRY
            97,     // LAUGH
            87,     // SURPRISED
            82,     // SAD
            87,     // AFRAID
            82,     // SHY
            92,     // LOVE
            87,     // CRY
            92,     // QUESTION
            82,     // HELLO
            92,     // SLEEP
            92,     // THINK
            87,     // SHOCK
            87,     // NORMAL DANCE (DANCE)
            87,     // EXERCISE (DANCE)
            87,     // GREETING (DANCE)
            87      // RHYTHM DANCE (DANCE)
    };

    public static int Left_Arm_Angle[] = {
            90,     // NONE
            100,    // SMILE
            108,    // ANNOYED
            75,     // ANGRY
            85,     // LAUGH
            75,     // SURPRISED
            105,    // SAD
            100,    // AFRAID
            110,    // SHY
            75,     // LOVE
            75,     // CRY
            95,     // QUESTION
            110,    // HELLO
            110,    // SLEEP
            100,    // THINK
            75,     // SHOCK
            90,     // NORMAL DANCE (DANCE)
            80,     // EXERCISE (DANCE)
            90,     // GREETING (DANCE)
            100     // RHYTHM DANCE (DANCE)
    };

    public static int Right_Arm_Angle[] = {
            90,     // NONE
            95,     // SMILE
            65,     // ANNOYED
            100,    // ANGRY
            95,     // LAUGH
            110,    // SURPRISED
            65,     // SAD
            95,     // AFRAID
            65,     // SHY
            75,     // LOVE
            110,    // CRY
            70,     // QUESTION
            120,    // HELLO
            65,     // SLEEP
            80,     // THINK
            110,    // SHOCK
            90,     // NORMAL DANCE (DANCE)
            90,     // EXERCISE (DANCE)
            90,     // GREETING (DANCE)
            90      // RHYTHM DANCE (DANCE)
    };

    // range 75(Right) ~ 105(Left)
    public static int Waist_Angle[] = {
            90,     // NONE
            95,     // SMILE
            85,     // ANNOYED
            90,     // ANGRY
            90,     // LAUGH
            90,     // SURPRISED
            65,     // SAD
            90,     // AFRAID
            80,     // SHY
            110,    // LOVE
            90,     // CRY
            100,    // QUESTION
            80,     // HELLO
            110,    // SLEEP
            90,     // THINK
            90,     // SHOCK
            90,     // NORMAL DANCE (DANCE)
            90,     // EXERCISE (DANCE)
            90,     // GREETING (DANCE)
            90      // RHYTHM DANCE (DANCE)
    };

    public static String Get_Action_Command(String emotion) {
        int index = Arrays.asList(Emotions).indexOf(emotion);
        return Get_Action_Command(index);
    }

    public static String Get_Action_Command(int index) {
        String emotion = Emotions[index];
        int left_ear = Left_Ear_Angle[index];
        int right_ear = Right_Ear_Angle[index];
        int neck = Neck_Angle[index];
        int left_arm = Left_Arm_Angle[index];
        int right_arm = Right_Arm_Angle[index];
        int waist = Waist_Angle[index];

        return emotion + "," + left_ear + ","+ right_ear + "," +
                neck + ","+ left_arm + "," + right_arm + "," + waist;
    }

    public static void Set_Action_Angle(int index, String text) {
        String[] actions = text.split(",");

        if (actions.length != 7)
            throw new StringIndexOutOfBoundsException();

        Left_Ear_Angle[index] = Integer.parseInt(actions[1]);
        Right_Ear_Angle[index] = Integer.parseInt(actions[2]);
        Neck_Angle[index] = Integer.parseInt(actions[3]);
        Left_Arm_Angle[index] = Integer.parseInt(actions[4]);
        Right_Arm_Angle[index] = Integer.parseInt(actions[5]);
        Waist_Angle[index] = Integer.parseInt(actions[6]);
    }
}
