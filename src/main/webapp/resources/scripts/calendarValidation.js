function validateHourRange() {

    console.log("validateHourRange function called");  // For debugging

    var calendarWidget = PF('calendarWidget');
    var selectedDate = calendarWidget.getDate();  // Get the selected date and time

    // Check if the hour is outside the allowed range (9 AM to 5 PM)
    var selectedHour = selectedDate.getHours();

    // If before 9 AM, set to 9 AM
    if (selectedHour < 9) {
        selectedDate.setHours(9);
    }

    // If after 5 PM, set to 5 PM
    if (selectedHour > 17) {
        selectedDate.setHours(17);
    }

    // Update the calendar widget with the corrected time
    calendarWidget.setDate(selectedDate);

    // Re-trigger change event on the input field to reflect the change
    calendarWidget.jq.trigger('change');
}
