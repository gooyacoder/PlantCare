package com.ahm.plantcare.Utils

object CommunicationKeys {
    // The format to be used for communication between activities: Sender_Receiver_Attribute
    // MAIN - NEWPLANT
    const val Main_NewPlant_RequestCode = 1
    const val NewPlant_Main_PlantPos = "intent_plant_pos"

    // MAIN - EDITPLANT
    const val Main_EditPlant_RequestCode = 2
    const val Main_EditPlant_ExtraPlantPosition = "intent_plant_to_edit_position"
    const val EditPlant_Main_PlantPrevPosition = "intent_edited_plant_prev_position"
    const val EditPlant_Main_PlantNewPosition = "intent_edited_plant_new_position"
    const val EditPlant_Main_ResultDelete = 3

    // MAIN - SETTINGS
    const val Main_Settings_RequestCode = 4
    const val Settings_Main_ResultDeleteAll = 5

    // NOTIFICATIONCLASS - WaterPlantService
    const val NotificationClass_WaterSinglePlantService_PlantToWater = "intent_plant_to_water"
}
