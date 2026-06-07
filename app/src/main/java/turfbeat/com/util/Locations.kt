package turfbeat.com.util

object Locations {
    val divisions = listOf(
        "Dhaka", "Chattogram", "Rajshahi", "Khulna",
        "Barishal", "Sylhet", "Rangpur", "Mymensingh"
    )

    val districts = mapOf(
        "Dhaka" to listOf("Dhaka", "Gazipur", "Narayanganj", "Tangail", "Kishoreganj", "Manikganj", "Munshiganj", "Narsingdi"),
        "Chattogram" to listOf("Chattogram", "Cox's Bazar", "Comilla", "Noakhali", "Brahmanbaria", "Chandpur", "Lakshmipur", "Feni"),
        "Rajshahi" to listOf("Rajshahi", "Bogra", "Pabna", "Natore", "Sirajganj", "Joypurhat", "Nawabganj"),
        "Khulna" to listOf("Khulna", "Jessore", "Kushtia", "Bagerhat", "Satkhira", "Jhenaidah", "Magura"),
        "Barishal" to listOf("Barishal", "Patuakhali", "Bhola", "Pirojpur", "Jhalokati", "Barguna"),
        "Sylhet" to listOf("Sylhet", "Moulvibazar", "Habiganj", "Sunamganj"),
        "Rangpur" to listOf("Rangpur", "Dinajpur", "Kurigram", "Lalmonirhat", "Nilphamari", "Panchagarh", "Thakurgaon"),
        "Mymensingh" to listOf("Mymensingh", "Jamalpur", "Netrokona", "Sherpur")
    )

    val areas = mapOf(
        "Dhaka" to listOf("Dhanmondi", "Gulshan", "Uttara", "Mirpur", "Banani", "Bashundhara", "Mohammadpur", "Motijheel", "Farmgate", "Malibagh", "Shyamoli", "Lalmatia", "Baridhara", "Tejgaon", "Kawran Bazar", "Paltan", "Wari", "Rampura", "Badda", "Khilgaon"),
        "Gazipur" to listOf("Tongi", "Kaliakair", "Sreepur", "Kapasia", "Kalihati"),
        "Narayanganj" to listOf("Siddhirganj", "Fatullah", "Bandar", "Rupganj", "Sonargaon"),
        "Chattogram" to listOf("Agrabad", "Halishahar", "Nasirabad", "Khulshi", "Pahartali", "Panchlaish", "Bakalia", "Chandgaon", "Patenga", "Double Mooring")
    )

    val positions = listOf("Striker", "Mid Fielder", "Defender", "Goal Keeper", "Any")
    val skillLevels = listOf("Beginner", "Intermediate", "Advanced")
    val matchFormats = listOf("5-a-side", "7-a-side", "9-a-side", "11-a-side", "Futsal")
}
