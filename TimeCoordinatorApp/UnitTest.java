import org.junit.Test;
import static org.junit.Assert.*;
import java.time.LocalTime;

public class UnitTest {
    // FileController tests
    @Test
    public void testRetrieveSettingsWhenNoFile() {
        assertTrue(FileController.RetrieveSettings() == null);
    }

    @Test
    public void testSaveFaultySettings() {
        assertTrue(FileController.SaveSettings("00 00 11 11 00 11") == "Settings were saved successfully");
    }

    @Test
    public void testRetrieveSettingsWhenFaulty() {
        assertTrue(FileController.RetrieveSettings() == null);
    }

    @Test
    public void testSaveFaultySettings2() {
        assertTrue(FileController.SaveSettings("01 01 01 01 01 01 01 01 01 01 01 01 01 01 01 01 01 01 01 01 01 01 01 011") == "Settings were saved successfully");
    }

    @Test
    public void testSaveValidSettings() {
        assertTrue(FileController.SaveSettings("01 01 01 01 01 01 01 01 01 01 01 01 01 01 01 01 01 01 01 01 01 01 01 01 ") == "Settings were saved successfully");
    }

    @Test
    public void testRetrieveSettingsWhenValid() {
        assertTrue(FileController.RetrieveSettings() != null);
    }


    // TimeZone tests
    @Test
    public void testConvertToUTC() {
        TimeZone timeZone = new TimeZone(5, 20);
        LocalTime time = LocalTime.of(12, 15);
        assertTrue(timeZone.ConvertToUTC(time).getHour() == 7);
        assertTrue(timeZone.ConvertToUTC(time).getMinute() == 15);
    }

    @Test
    public void testValidateTime1() {
        assertTrue(TimeZone.ValidateTime("11").formattedTime.getHour() == 11);
    }

    @Test
    public void testValidateTime2() {
        assertTrue(TimeZone.ValidateTime("1:30").formattedTime.getHour() == 1);
    }

    @Test
    public void testValidateTime3() {
        assertTrue(TimeZone.ValidateTime("16:").formattedTime.getHour() == 16);
    }

    @Test
    public void testValidateTime4() {
        assertTrue(TimeZone.ValidateTime("8").formattedTime.getHour() == 8);
    }

    @Test
    public void testValidateTime5() {
        assertTrue(TimeZone.ValidateTime("137").formattedTime.getMinute() == 37);
    }

    @Test
    public void testValidateTime6() {
        assertTrue(TimeZone.ValidateTime("1220").formattedTime.getMinute() == 20);
    }

    @Test
    public void testValidateTime7() {
        assertTrue(TimeZone.ValidateTime("12:20").formattedTime.getMinute() == 20);
    }

    @Test
    public void testInvalidTime1() {
        assertTrue(TimeZone.ValidateTime(":30").validTime == false);
    }

    @Test
    public void testInvalidTime2() {
        assertTrue(TimeZone.ValidateTime(":").validTime == false);
    }

    @Test
    public void testInvalidTime3() {
        assertTrue(TimeZone.ValidateTime("12.12").validTime == false);
    }
}
