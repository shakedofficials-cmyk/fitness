package com.recompos.app.data.local

object SeedData {
    val phases = listOf(
        ProgramPhaseEntity(1, "Foundation + Logbook", 1, 3, "Execution, full ROM, controlled eccentrics, most sets at 2 RIR.", "2 RIR", false),
        ProgramPhaseEntity(2, "Deload", 4, 4, "Reduce sets 40-50%, lighter loads, 3-4 RIR, no drop sets or failure.", "3-4 RIR", true),
        ProgramPhaseEntity(3, "Specialization Push", 5, 8, "Compounds 1-2 RIR. Isolation final sets can push hard. Chase clean rep PRs.", "1-2 RIR", false),
        ProgramPhaseEntity(4, "Deload", 9, 9, "Repeat the deload rules: lower volume, lighter loads, leave better than you arrived.", "3-4 RIR", true),
        ProgramPhaseEntity(5, "Peak Recomp Block", 10, 12, "Compounds at 1 RIR; isolations can reach technical failure without ugly reps.", "1 RIR", false)
    )

    val workouts = listOf(
        WorkoutTemplateEntity(1, 1, "Delts + Upper Chest + Triceps", "Side delts first in spirit, chest controlled, triceps clean.", 85, false),
        WorkoutTemplateEntity(2, 2, "Back Width + Biceps + Abs", "Elbows to hips, long lat positions, hard curls, loaded abs.", 90, false),
        WorkoutTemplateEntity(3, 3, "Legs + Calves", "Controlled quad work, hamstrings, calves with real pauses.", 85, false),
        WorkoutTemplateEntity(4, 4, "Rest / Steps / Mobility", "Walk, mobilize, breathe, and recover. No extra chest.", 20, true),
        WorkoutTemplateEntity(5, 5, "Back Thickness + Rear Delts + Biceps", "Thickness without turning every row into a lower-back contest.", 95, false),
        WorkoutTemplateEntity(6, 6, "Shoulders + Arms + Abs Specialization", "Shoulder-safe pressing, delts, arms, abs.", 85, false),
        WorkoutTemplateEntity(7, 7, "Rest", "Walk, eat, recover. No random junk volume.", 10, true)
    )

    val restTasks = listOf(
        RestTaskEntity(1, 4, 1, "8,000 steps minimum", "Move before cutting food aggressively."),
        RestTaskEntity(2, 4, 2, "Shoulder mobility", "10 minutes. External rotation, scap control, easy ranges."),
        RestTaskEntity(3, 4, 3, "Deep breathing", "5 minutes before sleep."),
        RestTaskEntity(4, 4, 4, "Log recovery", "Sleep, digestion/reflux, and bodyweight if due."),
        RestTaskEntity(5, 7, 1, "Walk", "Get easy movement without stealing recovery."),
        RestTaskEntity(6, 7, 2, "Eat", "Hit protein and keep digestion calm."),
        RestTaskEntity(7, 7, 3, "Recover", "No random junk volume.")
    )

    val exercises = listOf(
        e(101,1,1,"Incline Smith Press","upper chest","front delts,triceps",3,6,10,120,180,"2 then 1","Low-to-moderate incline. Controlled 2-3 sec lowering. Do not flare elbows hard.","No true failure. Shoulder-safe depth.","low-incline machine press; low-incline dumbbell press",false,false),
        e(102,1,2,"Low-Incline Dumbbell Press","upper chest","front delts,triceps",2,8,12,120,120,"1-2","Controlled lowering. No ego depth. Use shoulder-safe range.","Stop if shoulder irritation climbs.","machine incline press",false,false),
        e(103,1,3,"Cable Fly or Machine Fly","chest","front delts",2,12,15,90,90,"1-2","Smooth stretch and squeeze without shoulder pain.","Do not overdo chest volume.","pec deck; cable fly",false,true),
        e(104,1,4,"Single-Arm Cable Lateral Raise","side delts","upper traps",4,12,20,60,90,"1-2","Cable slightly behind body. Lead with elbow. Stop around shoulder height.","Pain >3/10 means stop and swap.","machine lateral raise",true,true),
        e(105,1,5,"Dumbbell Lateral Raise","side delts","upper traps",3,15,25,60,60,"1","Controlled. No swinging. Slight lean okay. Partials allowed after full reps in weeks 5-8 and 10-12.","Stop around shoulder height.","machine lateral raise; cable lateral raise",true,true),
        e(106,1,6,"Cable Rear-Delt Fly","rear delts","upper back",3,15,25,60,90,"1","Arms slightly bent. Pull out and back. Do not shrug.","Keep neck quiet.","reverse pec deck",false,true),
        e(107,1,7,"Rope Pushdown","triceps","",3,10,15,60,90,"1","Elbows pinned. Full lockout. Controlled return.","No elbow pain.","cable pushdown",false,true),
        e(108,1,8,"Overhead Cable Extension or Katana Extension","triceps long head","",2,12,20,60,90,"1","Long-head stretch. Smooth reps.","No elbow pain.","katana extension; overhead rope extension",false,true),

        e(201,2,1,"Neutral-Grip or Semi-Supinated Lat Pulldown","lats","biceps",3,8,12,120,120,"1-2","Drive elbows down, not back. Think elbows to hips.","Do not turn it into a row.","assisted pull-up; machine pulldown",false,false),
        e(202,2,2,"Single-Arm Cable Lat Pulldown","lats","biceps",3,10,15,90,90,"1-2","Stretch at top. Elbow to hip. Match sides.","Control torso rotation.","single-arm machine pulldown",false,true),
        e(203,2,3,"Barbell Row","mid-back","lats,hamstrings",3,6,10,120,180,"1-2","Strict form. No hip-thrust rowing.","Avoid lower-back fatigue. No true failure.","chest-supported row",false,false),
        e(204,2,4,"Wide-Grip Seated Cable Row","upper back","rear delts",2,10,15,120,120,"1-2","Elbows out. Upper-back focus.","Keep ribs down.","machine high row",false,false),
        e(205,2,5,"Straight-Arm Pulldown","lats","triceps",2,12,20,60,90,"1","Keep arms long. Feel lats. Avoid triceps dominance.","Smooth shoulder motion only.","cable pullover",false,true),
        e(206,2,6,"Incline Dumbbell Curl","biceps long head","forearms",3,10,15,90,90,"1","Full stretch. No shoulder swinging.","Keep upper arm still.","bayesian cable curl",true,true),
        e(207,2,7,"Preacher Curl","biceps","forearms",3,8,12,90,90,"1","Controlled lower. No bouncing.","Protect elbows at bottom.","machine preacher curl",true,true),
        e(208,2,8,"Rope Hammer Curl","brachialis","biceps,forearms",2,12,15,60,90,"1","Neutral grip. Control.","No body English.","dumbbell hammer curl",true,true),
        e(209,2,9,"Cable Crunch","abs","",3,10,15,60,90,"1","Progress like a muscle. Spinal flexion. Do not just hip hinge.","Keep hips stable.","machine crunch",false,true),

        e(301,3,1,"Squat or Smith Squat","quads","glutes",3,5,8,180,180,"1-2","Controlled depth. Brace hard.","Avoid lower-back overload. No true failure.","Smith squat; hack squat",false,false),
        e(302,3,2,"Leg Press","quads","glutes",3,10,15,120,180,"1-2","Controlled deep reps. Do not lock out and rest at top.","No knee collapse.","hack squat",false,false),
        e(303,3,3,"Leg Extension","quads","",3,12,20,90,90,"1","Hard squeeze. Final set drop allowed in weeks 5-8.","Control knee comfort.","single-leg extension",true,true),
        e(304,3,4,"Lying Leg Curl","hamstrings","",3,8,12,90,90,"1","Control the lower. Hips down.","No bouncing.","seated leg curl",false,true),
        e(305,3,5,"Seated Leg Curl","hamstrings","",2,12,20,90,90,"1","Full stretch. Strong squeeze.","Control pelvis.","lying leg curl",false,true),
        e(306,3,6,"Standing Calf Raise","calves","",4,8,12,90,90,"1","Pause at stretched bottom. Do not bounce.","Use full ROM.","leg press calf raise",false,true),
        e(307,3,7,"Seated Calf Raise","calves","",3,12,20,60,90,"1","Pause bottom and top.","Do not rush reps.","standing calf raise",false,true),

        e(501,5,1,"Romanian Deadlift","hamstrings","glutes,back thickness",3,6,10,180,180,"1-2","Strong but controlled. Hinge cleanly.","Do not make every week a lower-back survival contest. No true failure.","machine RDL; hip hinge machine",false,false),
        e(502,5,2,"Seated Cable Row","mid-back","lats",3,8,12,120,120,"1-2","Stable torso. Pull elbows back.","No momentum rows.","chest-supported row",false,false),
        e(503,5,3,"Wide or Neutral Lat Pulldown","lats","biceps",3,8,12,120,120,"1-2","Own the stretch and drive down.","Do not yank neck forward.","machine pulldown",false,false),
        e(504,5,4,"One-Arm Dumbbell Row","lats","mid-back",3,8,12,90,120,"1-2","Hand supported on bench. Pull toward hip.","Not chest-supported. Keep low back controlled.","one-arm cable row",false,false),
        e(505,5,5,"Rear-Delt Cable Row","rear delts","upper back",3,12,20,90,90,"1","Pull toward upper chest/face. Elbows high.","Do not shrug.","face pull",false,true),
        e(506,5,6,"Face Pull","rear delts","external rotators,upper back",2,15,25,60,60,"1-2","Pull to face. Rotate smoothly.","Shoulders down.","rear-delt fly",false,true),
        e(507,5,7,"Barbell Curl","biceps","forearms",3,6,10,120,120,"1","Strong strict curls.","No hip pop.","EZ-bar curl",true,true),
        e(508,5,8,"Bayesian Cable Curl","biceps long head","",2,12,20,60,90,"1","Arm behind body. Full stretch.","No shoulder swinging.","incline dumbbell curl",true,true),
        e(509,5,9,"Hanging Knee Raise or Captain's Chair Raise","abs","hip flexors",3,8,15,60,90,"1","Posterior pelvic tilt at top. Do not swing.","Control hip flexors.","reverse crunch",false,true),

        e(601,6,1,"Neutral-Grip Seated Dumbbell Press or Landmine Press","shoulders","triceps",2,8,12,120,120,"2","No heavy barbell overhead pressing. Use landmine if lowering overhead hurts.","Left shoulder priority. No ego lowering.","landmine press; neutral-grip machine press",false,false),
        e(602,6,2,"Cable Lateral Raise","side delts","",4,12,20,60,90,"1","Lead with elbow. Smooth reps.","Pain >3/10 means stop and swap.","machine lateral raise",true,true),
        e(603,6,3,"Dumbbell Lateral Raise","side delts","",2,15,25,60,60,"1","Controlled. Final set partials allowed in weeks 10-12.","Stop around shoulder height.","cable lateral raise",true,true),
        e(604,6,4,"Rear-Delt Fly","rear delts","upper back",2,15,25,60,90,"1","Pull out and back.","Do not shrug.","reverse pec deck",false,true),
        e(605,6,5,"Preacher Curl","biceps","",3,8,12,90,90,"1","Controlled lower. No bouncing.","Protect elbows.","machine preacher curl",true,true),
        e(606,6,6,"Incline Hammer Curl","brachialis","biceps",2,10,15,90,90,"1","Neutral grip. Full control.","No swinging.","rope hammer curl",true,true),
        e(607,6,7,"Cable Pushdown","triceps","",2,12,20,60,90,"1","Lockout with control.","No elbow pain.","rope pushdown",false,true),
        e(608,6,8,"Katana Extension","triceps long head","",2,12,20,60,90,"1","Long-head stretch.","No elbow pain.","overhead cable extension",false,true),
        e(609,6,9,"Weighted Cable Crunch","abs","",3,10,15,60,90,"1","Load and flex spine.","Do not just hinge.","machine crunch",false,true),
        e(610,6,10,"Ab Wheel or Long-Lever Plank","abs/core","",2,8,12,60,90,"1","Brace hard. Plank option 30-60 seconds.","Keep lumbar position clean.","long-lever plank; dead bug",false,true)
    )

    val articles = listOf(
        KnowledgeArticleEntity(1, "Why recomp, not bulk", "You are prioritizing lean gain with waist control because the baseline includes tummy, high triglycerides, and lower activity. The win is stable-to-slow bodyweight, waist control, and rising performance."),
        KnowledgeArticleEntity(2, "RIR guide", "3 RIR is easy with three reps left. 2 RIR is challenging and clean. 1 RIR is very hard with one good rep left. 0 RIR is technical failure. Compounds should not hit true failure."),
        KnowledgeArticleEntity(3, "Double progression", "Keep the same load until every working set reaches the top of the rep range at the target RIR. Then increase load next time. Compounds jump 2.5-5 kg; isolations earn reps first."),
        KnowledgeArticleEntity(4, "Deload guide", "Weeks 4 and 9 reduce sets 40-50%, keep 3-4 RIR, remove drop sets and failure, and leave the gym feeling better."),
        KnowledgeArticleEntity(5, "Shoulder rules", "Pain above 3/10 means stop and swap. No heavy barbell overhead pressing early. Favor cables, landmine, neutral grip, rear delts, and controlled laterals."),
        KnowledgeArticleEntity(6, "Nutrition rules", "Training days: 2600 kcal, 190 g protein, 300 g carbs, 65-70 g fat. Rest days: 2300-2400 kcal, 190 g protein, 220-240 g carbs, 70-75 g fat. Place carbs around training."),
        KnowledgeArticleEntity(7, "Reflux guide", "Use smaller evening meals, reduce spicy/fried/high-fat meals late, avoid huge shakes near bedtime, and log triggers."),
        KnowledgeArticleEntity(8, "Triglyceride lifestyle", "Control calories, limit refined sugar overeating, avoid alcohol if applicable, increase steps/cardio, and discuss medical management with a physician if levels are high."),
        KnowledgeArticleEntity(9, "Supplements", "Creatine monohydrate 5 g daily. Whey only to hit protein. Whey isolate may help bloating. Vitamin D and omega-3 dosing should be discussed with a licensed clinician.")
    )

    private fun e(
        id: Int,
        workoutId: Int,
        order: Int,
        name: String,
        target: String,
        secondary: String,
        sets: Int,
        minReps: Int,
        maxReps: Int,
        restMin: Int,
        restMax: Int,
        rir: String,
        cues: String,
        cautions: String,
        alternatives: String,
        drop: Boolean,
        failure: Boolean
    ) = ExerciseTemplateEntity(
        id = id,
        workoutTemplateId = workoutId,
        orderIndex = order,
        name = name,
        targetMuscle = target,
        secondaryMuscles = secondary,
        sets = sets,
        minReps = minReps,
        maxReps = maxReps,
        restSecondsMin = restMin,
        restSecondsMax = restMax,
        rirTarget = rir,
        cues = cues,
        cautions = cautions,
        alternatives = alternatives,
        dropSetAllowed = drop,
        failureAllowed = failure
    )
}
