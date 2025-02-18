// Time imports
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.ZoneId;
// Input and output imports
import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;
// UI imports
import javax.swing.*;
import java.awt.*;

// Main
public class CoordinatorApp {
    public static void main(String[] args) {
        new Coordinator().SetupApp();
    }
}

class Coordinator {

    private CentralUI centralUI = null;
    private TimeZone[] timeZones = new TimeZone[24];
    private String settingsString = null;

    public void SetupApp() {

        // Initialize centralUI
        centralUI = new CentralUI(this);

        // Call InitializeTimeZones()
        InitializeTimeZones();

        // Call centralUI.BuildTimeZoneBoxes()
        centralUI.BuildTimeZoneBoxes(timeZones);

        // Get settings from FileController.RetrieveSettings()
        String settings = FileController.RetrieveSettings();


        // If settings are found 
        if (settings != null) {
            //Call ApplySettings()
            ApplySettings(settings);
        }

        // Get the current time from GetCurrentTimeUTC()
        LocalTime currentTime = GetCurrentTimeUTC();
        //Call ConvertTimes() with this time
        ConvertTimes(currentTime);
    }

    private void InitializeTimeZones() {

        // Intialize distance from UTC counter
        int hoursFromUTC = TimeZoneData.FIRST_HOURS_FROM_UTC;

		// For each 24 time zones
        for (int i = 0; i < 24; i++) {
			// Initialize timeZone including:
			// Set differenceFromUTC to value from setup table
			// Set offsetAmount to value from setup table
            TimeZone zone = new TimeZone(hoursFromUTC, TimeZoneData.OFFSET_MINUTES[i]);

			//Add timeZone to the list of timeZones
            timeZones[i] = zone;

            // Adjust distance from UTC
            hoursFromUTC++;

		}
    }

    private LocalTime GetCurrentTimeUTC(){
        // Get UTC time
        ZonedDateTime currentDateTimeUTC = ZonedDateTime.now(ZoneId.of("UTC"));
        // Convert to LocalTime type and return
        LocalTime currentTime = currentDateTimeUTC.toLocalTime();
        return currentTime;
    }

    public void ConvertTimes(LocalTime timeUTC) {
        // For each of the 24 timeZones
        for (int i = 0; i < 24; i++) {
			// Call timeZone.ConvertToZone()
            timeZones[i].ConvertToZone(timeUTC);
		}
    }

    private void ApplySettings(String settings) {
        // For each of the 24 timeZones
        for (int i = 0; i < 24; i++) {
			// Parse the settings string by spaces
            String[] sections = settings.split(" ");

			// Get the substring with equivalent index to the timeZone
            // Get the substring with equivalent index to the time zone
            String zoneSettings = sections[i];
                
            // If the first substring digit is "1"
            if (zoneSettings.charAt(0) == '1') {
                // Call timeZone.ToggleHighlight()
                timeZones[i].ToggleHighlight();
            }
            
            // If the second substring digit is "1"
            if (zoneSettings.charAt(1) == '1') {
                // Call timeZone.ToggleOffset()
                timeZones[i].ToggleOffset();
            }

        }

    }

    public void GetCurrentSettings() {
        // Initialize settings string
        String currentSettings = "";

		//For each of the 24 timeZones
        for (int i = 0; i < 24; i++) {
			// If the timeZone is highlighted
            if (timeZones[i].IsHighlighted()) {
				// Append a "1" to the string
                currentSettings += "1";
            }
			else {
				// Append a "0" to the string
                currentSettings += "0";
            }
            
			//If offset is enabled
            if (timeZones[i].IsOffsetEnabled()) {
				// Append a "1" to the string
                currentSettings += "1";
            }
			else {
				// Append a "0" to the string
                currentSettings += "0";
            }

			// Append a space to the string
            currentSettings += " ";
		}

        // Store settings string
		settingsString = currentSettings;

    }

    public String SaveCurrentSettings() {
        // Get status from FileController.SaveSettings() using the stored settings string
        String status = FileController.SaveSettings(settingsString);
        // Return status
        return status;
    }
}

class TimeZone {

    private final int hoursFromUTC;
    private final int offsetMinutes;

    private boolean offsetEnabled = false;
    private boolean highlighted = false;
    // Initialize time to 00:00
    private LocalTime time = LocalTime.MIDNIGHT;
    private TimeZoneBox associatedBox = null;

    public TimeZone(int hoursFromUTC, int offsetMinutes) {
        this.hoursFromUTC = hoursFromUTC;
        this.offsetMinutes = offsetMinutes;
    }


    public void SetAssociatedBox(TimeZoneBox associatedBox) {
        this.associatedBox = associatedBox;
    }

    public LocalTime ConvertToUTC(LocalTime zoneTime) {

		// Subtract hoursFromUTC from the given time
        LocalTime utcTime = zoneTime.minusHours(hoursFromUTC);

		// If offset is enabled
        if (offsetEnabled == true) {
			//Subtract offsetMinutes from the new time
            utcTime = utcTime.minusMinutes(offsetMinutes);
        }

		//Return the new time
        return utcTime;
	}

    public void ConvertToZone(LocalTime utcTime) {

		// Add hoursFromUTC from the given time
        LocalTime zoneTime = utcTime.plusHours(hoursFromUTC);

		// If offset is enabled
        if (offsetEnabled == true) {
			// Add offsetMinutes to the new time
            zoneTime = zoneTime.plusMinutes(offsetMinutes);
		}

		// Store new time
        time = zoneTime;
        //Call timeZoneBox.DisplayTime() using the new time
        associatedBox.DisplayTime(time);
	}

    public boolean IsHighlighted() {
        return highlighted;
    }

    public boolean IsOffsetEnabled() {
        return offsetEnabled;
    }

    public void ToggleHighlight() {
		// If highlighted is false
        if (!highlighted) {
            // Set highlighted to true
            highlighted = true;
			// Call timeZoneBox.HighlightBox()
            associatedBox.HighlightBox();
        }
		else {
			// Set highlighted to false
            highlighted = false;
			// Call timeZoneBox.UnhighlightBox()
            associatedBox.UnhighlightBox();
        }
	}

    public void ToggleOffset() {
		// If offsetEnabled is false
        if (!offsetEnabled) {
			// Set offsetEnabled to true
            offsetEnabled = true;

			// Add offsetAmount to time
            // And store new time
            time = time.plusMinutes(offsetMinutes);

			// Call timeZoneBox.DisplayTime() using the new time
            associatedBox.DisplayTime(time);
			// Call timeZoneBox.ShowOffsetOn()
            associatedBox.ShowOffsetOn();
        }
		else {
			// Set offsetEnabled to false
            offsetEnabled = false;
            
			// Subtract offsetAmount from time
            // And store new time
            time = time.minusMinutes(offsetMinutes);

			// Call timeZoneBox.DisplayTime() using the new time
            associatedBox.DisplayTime(time);
			// Call timeZoneBox.ShowOffsetOn()
            associatedBox.ShowOffsetOff();
        }
	}

    public static ValidateResults ValidateTime(String input) {
        int hours = 0;
        int minutes = 0;

        // If input is a number
        if (input.matches("\\d+")) {
            switch (input.length()) {
                // If input length is 1 or 2
                case 1:
                case 2:
                    // Set hours equal to input
                    hours = Integer.parseInt(input);
                    break;
                // Else if input length is 3
                case 3:
                    // Set hours equal to first digit
                    hours = Integer.parseInt(input.substring(0, 1));
                    // Set minutes equal to second and third digit
                    minutes = Integer.parseInt(input.substring(1, 3));
                    break;
                // Otherwise if input is longer
                default:
                    // Set hours equal to first two digits
                    hours = Integer.parseInt(input.substring(0, 2));
                    // Set minutes equal to digits 3 and 4
                    minutes = Integer.parseInt(input.substring(2, 4));
                    break;
            }
        }
		// Else if input is all numbers besides a colon
        else if (input.matches("^\\d*:\\d*$")) {

            // If the first character is the colon
            if (input.charAt(0) == ':') {
                // Return invalid message
                ValidateResults results = new ValidateResults(false, null, "Time cannot start with a colon");
                return results;
            }

            // Split string around colon
            String[] parts = input.split(":");
            // Set hours equal to digits before colon
            hours = Integer.parseInt(parts[0]);

            // If the last character is not the colon
            if (parts.length > 1) {
                // Set minutes to all digits after colon
                minutes = Integer.parseInt(parts[1]);
            }
        } 
        else {
            // Return invalid message
            ValidateResults results = new ValidateResults(false, null, "Time must only contain numbers with optional colon");
            return results;
        }
				
		// If hours is not in the range of 0 to 23
        if (hours < 0 || hours > 23) {
            // Return hours outside of bounds message
            ValidateResults results = new ValidateResults(false, null, "Hours not in valid range");
            return results;
        }
        // If minutes is not in the range of 0 to 59
        if (minutes < 0 || minutes > 59) {
            // Return minutes outside of bounds message
            ValidateResults results = new ValidateResults(false, null, "Minutes not in valid range");
            return results;
        }

        // Build time using hours and minutes
        LocalTime formattedInput = LocalTime.of(hours, minutes);
        ValidateResults results = new ValidateResults(true, formattedInput, "");
		// Return time
        return results;
	}

}

// FileController works as a static class
class FileController {

    private static final String SAVE_FILE_PATH = "SaveFile.txt";
    
    public static String SaveSettings(String settings) {

        String status = "";

        try {
            // Get file location
            File saveFile = new File(SAVE_FILE_PATH);

            //If save file does no exist
            //Create save file
            saveFile.createNewFile();

            //Open file
            FileWriter saveWriter = new FileWriter(SAVE_FILE_PATH);

            //Empty file
            //Write settings string to file
            saveWriter.write(settings);

            //Close file
            saveWriter.close();

            //Set status to completed
            status = "Settings were saved successfully";
        } 
        catch(Exception exception) {
            // Return bad status message
            status = "Unable to save settings.";
        }

        return status;
	}

    public static String RetrieveSettings() {

        try{
            // Get file location
            File saveFile = new File(SAVE_FILE_PATH);

            //If save file does not exist
            if (saveFile.exists() == false) {

                return null;
            }

            // Open file
            Scanner saveReader = new Scanner(saveFile);
            
            // Declare string
            String settings = null;

            // If file has data
            if (saveReader.hasNextLine()) {
                // Read file into string
                settings = saveReader.nextLine();

                // Check if settings are valid
                boolean validSettings = ValidateSettings(settings);

                // If settings are invalid
                if (validSettings == false) {
                    // Nullify settings
                    settings = null;
                }
            }

            // Close file
            saveReader.close();

            return settings;
        }
        catch(Exception exception){
            return null;
        }
    }

    public static boolean ValidateSettings(String settings) {

        final int correctLength = 72;

		// If the length of the string is not 72
        if (settings.length() != correctLength) {
			return false;
        }

		// If the string is in the form (0 or 1)(0 or 1)(space) repeated
        if (settings.matches("([01][01] )*")) {
				return true;
        }
		else {
			return false;
        }
	}
}

class CentralUI {

    private final Coordinator coordinator;

    private JLabel statusLabel = null;

    CentralUI(Coordinator coordinator) {
        this.coordinator = coordinator;
    }

    public void BuildTimeZoneBoxes(TimeZone[] timeZones) {
        // Create window
        JFrame windowFrame = new JFrame("Time Zone Coordinator");
        windowFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Define window default size
        windowFrame.setSize(DesignSizes.FRAME_WIDTH, DesignSizes.FRAME_HEIGHT);

        // Create grid panel with 4 rows and 6 columns for the 24 time zone boxes
        JPanel gridPanel = new JPanel(new GridLayout(4, 6, DesignSizes.SMALL_GAP, DesignSizes.SMALL_GAP));
        // For each of the 24 time zones
        for (int i = 0; i < 24; i++) {
            
            // Create button for time zone
            JButton zoneButton = new JButton();
            // Set button's normal size
            zoneButton.setPreferredSize(new Dimension(DesignSizes.BOX_WIDTH, DesignSizes.BOX_HEIGHT));
            // Make the button able to contain shifted content
            zoneButton.setLayout(new BorderLayout());

            // Create panel for offset button
            JPanel zoneOffsetPanel = new JPanel(new BorderLayout());
            // Erase panel default background
            zoneOffsetPanel.setOpaque(false);

            // Declare offsetButton here so it can be accessed by TimeZoneBox constructor
            JButton offsetButton = null;

            // Only build offset button if there is a defined icon
            if (TimeZoneData.OFFSET_ICONS_DISABLED[i] != null) {
                // Create offset button icon
                ImageIcon icon = new ImageIcon(TimeZoneData.ICON_PATH + TimeZoneData.OFFSET_ICONS_DISABLED[i]);

                // Create offset button using icon
                offsetButton = new JButton("", icon);
                // Set offset button size to the defined small control dimemsions
                offsetButton.setPreferredSize(new Dimension(DesignSizes.SHORT_NORMAL, DesignSizes.SHORT_NORMAL));

                // Set offset button background color
                offsetButton.setBackground(DesignColors.WHITE);
    
                // Add offset to right side of panel
                zoneOffsetPanel.add(offsetButton, BorderLayout.EAST);
            }
            else {
                // Create spacing so all zones look similiar
                zoneOffsetPanel.add(Box.createRigidArea(new Dimension(0, DesignSizes.SHORT_NORMAL))); 
            }
            // Add offset panel to zone button
            zoneButton.add(zoneOffsetPanel, BorderLayout.NORTH);
            

            // Create panel to contain zone info and time input
            JPanel zoneInfoPanel = new JPanel();
            // Erase panel default background
            zoneInfoPanel.setOpaque(false);
            // Make layout vertical list
            zoneInfoPanel.setLayout(new BoxLayout(zoneInfoPanel, BoxLayout.Y_AXIS));

            // Create label of time zone's abbreviation
            JLabel zoneNameLabel = new JLabel(TimeZoneData.ZONE_NAMES[i]);
            // Center label
            zoneNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            // Set font to bold and large
            zoneNameLabel.setFont(new Font("Arial", Font.BOLD, DesignFontSizes.TITLE));
            // Add zone name label to info panel
            zoneInfoPanel.add(zoneNameLabel);

            // Create label of time zone's example city
            JLabel cityLabel = new JLabel(TimeZoneData.ZONE_CITIES[i]);
            // Center label
            cityLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            // Set font to plain and small
            cityLabel.setFont(new Font("Arial", Font.PLAIN, DesignFontSizes.SMALL));
            // Add zone city label to info panel
            zoneInfoPanel.add(cityLabel);

            // Add space between city name and time display/input
            zoneInfoPanel.add(Box.createRigidArea(new Dimension(0, DesignSizes.SMALL_GAP))); 

            // Create and initialize time display/input field
            JTextField timeField = new JTextField("00:00");
            // Adjust size of field to standards
            timeField.setMaximumSize(new Dimension(DesignSizes.LONG_NORMAL, DesignSizes.SHORT_NORMAL));
            // Center display
            timeField.setHorizontalAlignment(SwingConstants.CENTER);
            // Add time display field to info panel
            zoneInfoPanel.add(timeField);

            // Add info panel to center of zone button
            zoneButton.add(zoneInfoPanel, BorderLayout.CENTER);

            // Add zone button to grid
            gridPanel.add(zoneButton);

            // Build TimeZoneBox object (it will associate itself with a TimeZone object)
            new TimeZoneBox(i, timeZones[i], coordinator, this, zoneButton, timeField, offsetButton);
        
        }

        // Add time zone grid to center of window
        windowFrame.add(gridPanel, BorderLayout.CENTER);

        // Create bottom bar
        // Border layout allows settings button to be left-shifted
        JPanel bottomPanel = new JPanel(new BorderLayout(DesignSizes.LARGE_GAP, DesignSizes.LARGE_GAP));

        // Create settings button
        JButton settingsButton = new JButton("Save Settings");
        // Color settings button
        settingsButton.setBackground(DesignColors.SOFT_CYAN);
        // Fix weird default visual behavior
        settingsButton.setFocusPainted(false);

        // Create status label with no default text
        statusLabel = new JLabel("");
        // Color status label
        statusLabel.setForeground(DesignColors.SOFT_GREEN);

        // Set up settings button event
        settingsButton.addActionListener(event -> SaveButtonPressed());

        // Put settings button to left side
        bottomPanel.add(settingsButton, BorderLayout.WEST);
        // Put status label to right of settings button
        bottomPanel.add(statusLabel, BorderLayout.CENTER);

        // Add bottom bar to bottom of window
        windowFrame.add(bottomPanel, BorderLayout.SOUTH);

        // Make the window visible
        windowFrame.setVisible(true);

    }

    public void SetStatusLabel(String status) {
        // Set status label
        statusLabel.setText(status);

        // Hide status label in 3 seconds
        Timer timer = new Timer(3000, event -> statusLabel.setText(""));
        timer.setRepeats(false);
        timer.start();
    }

    private void SaveButtonPressed() {
        // Get most up-to-date settings
        coordinator.GetCurrentSettings();
		// Save the settings
        String status = coordinator.SaveCurrentSettings();
        // Display status
        SetStatusLabel(status);
    }
}

class TimeZoneBox {

    private final int id;

    private final Coordinator coordinator;
    private final CentralUI centralUI;
    private final TimeZone associatedTimeZone;

    private final JButton zoneButton;
    private final JTextField timeField;
    private final JButton offsetButton;


    public TimeZoneBox(int id, TimeZone associatedTimeZone, Coordinator coordinator, CentralUI centralUI, JButton zoneButton, JTextField timeField, JButton offsetButton) {
        this.id = id;
        this.zoneButton = zoneButton;
        this.timeField = timeField;
        this.offsetButton = offsetButton;

        this.coordinator = coordinator;
        this.centralUI = centralUI;
        this.associatedTimeZone = associatedTimeZone;
        // Associate box with TimeZone
        associatedTimeZone.SetAssociatedBox(this);

        // Set up zone button clicked event
        zoneButton.addActionListener(event -> HighlightPressed());

        // Set up enter pressed in time field event
        timeField.addActionListener(event -> TimeEntered());

        // If there is an offset button
        if (offsetButton != null) {
            // Set up offset button clicked event
            offsetButton.addActionListener(event -> OffsetPressed());
        }
    }


    public void DisplayTime(LocalTime time) {
        // Format time into hour and minute string for display
        String displayString = String.format("%02d:%02d", time.getHour(), time.getMinute());
        // Display time
        timeField.setText(displayString);
    }
    
    public void HighlightBox() {
        // Change button color
        zoneButton.setBackground(DesignColors.YELLOW);
    }

    public void UnhighlightBox() {
        // Setting button background to null returns it to the default design
        zoneButton.setBackground(null);
    }

    public void ShowOffsetOn() {
        // Ensure offset is allowed
        if (TimeZoneData.OFFSET_ICONS[id] != null) {
            // Create enabled offset button icon
            ImageIcon icon = new ImageIcon(TimeZoneData.ICON_PATH + TimeZoneData.OFFSET_ICONS[id]);
            // Set offset button icon
            offsetButton.setIcon(icon);
        }
    }
    
    public void ShowOffsetOff() {
        // Create disabled offset button icon
        ImageIcon icon = new ImageIcon(TimeZoneData.ICON_PATH + TimeZoneData.OFFSET_ICONS_DISABLED[id]);
        // Set offset button icon
        offsetButton.setIcon(icon);
    }

    private void HighlightPressed() {
        associatedTimeZone.ToggleHighlight();
    }

    private void OffsetPressed() {
        associatedTimeZone.ToggleOffset();
    }

    private void TimeEntered() {
        // Validate the input
        ValidateResults validateResults = TimeZone.ValidateTime(timeField.getText());

        // If the input is valid
        if (validateResults.validTime == true) {
            // Convert time to UTC
            LocalTime inputUTC = associatedTimeZone.ConvertToUTC(validateResults.formattedTime);
            // Convert all times to input
            coordinator.ConvertTimes(inputUTC);
        }
        else {
            // Display warning
            centralUI.SetStatusLabel(validateResults.warning);
        }
    }
}

// Class that enables passing of results from TimeZone.ValidateResults()
class ValidateResults {
    
    // Whether time is valid
    public final boolean validTime;
    // Input translated to LocalTime if valid
    public final LocalTime formattedTime;
    // Warning string if not valid
    public final String warning;

    public ValidateResults(boolean validTime, LocalTime formattedTime, String warning) {
        this.validTime = validTime;
        this.formattedTime = formattedTime;
        this.warning = warning;
    }
}

// Design colors used
final class DesignColors {
    public static final Color SOFT_CYAN = new Color(224, 255, 255);
    public static final Color WHITE = new Color(255, 255, 255);
    public static final Color YELLOW = new Color(222, 202, 87);
    public static final Color SOFT_GREEN = new Color(13, 152, 186);
}

// Design fonts used
final class DesignFontSizes {
    public static final int TITLE = 16;
    public static final int SMALL = 10;
}

// Design defined size specifications
final class DesignSizes {
    public static final int BOX_WIDTH = 90;
    public static final int BOX_HEIGHT = 60;
    public static final int SHORT_NORMAL = 20;
    public static final int LONG_NORMAL = 50;
    public static final int FRAME_WIDTH = 650;
    public static final int FRAME_HEIGHT = 450;
    public static final int LARGE_GAP = 10;
    public static final int SMALL_GAP = 5;
}

// Zone data used by the application accessed by each timeZone's id or index in a loop
final class TimeZoneData {
    public static final String ICON_PATH = "Icons/";

    public static final String[] ZONE_NAMES = {"NUT", "HST", "AKST", "PST", "MST", "CST(US)", 
                                                "EST", "AST", "ART", "GST(SG)", "AZOT", "GMT", 
                                                "CET", "EET", "MSK", "GST(AE)", "PKT", "BST", 
                                                "ICT", "CST(CN)", "JST", "AEST", "NCT", "NZST"};

    public static final String[] ZONE_CITIES = {"Pago Pago", "Honolulu", "Anchorage", "Los Angeles", "Denver", "Chicago", 
                                                "New York City", "Caracus", "Buenos Aires", "South Georgia", "Azores", "London", 
                                                "Paris", "Cairo", "Moscow", "Dubai", "Karachi", "Dhaka", 
                                                "Bangkok", "Beijing", "Tokyo", "Sydney", "Noumea", "Auckland"};

    public static final String[] OFFSET_ICONS = {null, null, "sun.png", "sun.png", "sun.png", "sun.png", 
                                                "sun.png", "sun.png", "newFoundland.png", "sun.png", "sun.png", "sun.png", 
                                                "sun.png", "sun.png", "iran.png", "afghanistan.png", "india.png", "nepal.png", 
                                                "myanmar.png", null, "southAustralia.png", "sun.png", null, null};   

    public static final String[] OFFSET_ICONS_DISABLED = {null, null, "sun_disabled.png", "sun_disabled.png", "sun_disabled.png", "sun_disabled.png", 
                                                    "sun_disabled.png", "sun_disabled.png", "newFoundland_disabled.png", "sun_disabled.png", "sun_disabled.png", "sun_disabled.png", 
                                                    "sun_disabled.png", "sun_disabled.png", "iran_disabled.png", "afghanistan_disabled.png", "india_disabled.png", "nepal_disabled.png", 
                                                    "myanmar_disabled.png", null, "southAustralia_disabled.png", "sun_disabled.png", null, null};

    public static final int FIRST_HOURS_FROM_UTC = -11;

    public static final int[] OFFSET_MINUTES = {0, 0, 60, 60, 60, 60,
                                                60, 60, -30, 60, 60, 60,
                                                60, 60, 30, 30, 30, -15,
                                                -30, 0, 30, 60, 0, 0};

}