import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class BusinessDayCalculator {
    private final HolidayRepository holidayRepository;  // Interface to access holiday data
    
    public BusinessDayCalculator(HolidayRepository holidayRepository) {
        this.holidayRepository = holidayRepository;
    }
    
    /**
     * Gets the next business day based on the input date and number of days to add
     * @param date Starting date
     * @param numberOfDays Number of business days to add
     * @param holidayType 1 for broker holidays, 2 for bank holidays
     * @return Next business date
     */
    public LocalDate getNextBusinessDay(LocalDate date, Integer numberOfDays, Integer holidayType) {
        // Default values handling
        LocalDate startDate = Optional.ofNullable(date).orElse(LocalDate.now());
        int daysToAdd = Optional.ofNullable(numberOfDays).orElse(1);
        int holidayFlag = Optional.ofNullable(holidayType).orElse(1);
        
        if (daysToAdd == 0) {
            return isBusinessDay(startDate, holidayFlag) ? 
                   startDate : 
                   getNextBusinessDay(startDate, 1, holidayFlag);
        }
        
        LocalDate tempDate = startDate;
        int remainingDays = daysToAdd;
        
        while (remainingDays > 0) {
            tempDate = tempDate.plusDays(1);
            
            // Skip weekends and holidays
            while (!isBusinessDay(tempDate, holidayFlag)) {
                tempDate = tempDate.plusDays(1);
            }
            
            remainingDays--;
        }
        
        return tempDate;
    }
    
    /**
     * Checks if the given date is a holiday
     * @param date Date to check
     * @param holidayType 1 for broker holidays, 2 for bank holidays
     * @return true if it's a holiday
     */
    private boolean isHoliday(LocalDate date, int holidayType) {
        return holidayRepository.isHoliday(date, holidayType == 2);
    }
    
    /**
     * Checks if the given date is a business day
     * @param date Date to check
     * @param holidayType 1 for broker holidays, 2 for bank holidays
     * @return true if it's a business day
     */
    private boolean isBusinessDay(LocalDate date, int holidayType) {
        // Check if it's weekend
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            return false;
        }
        
        // Check if it's a holiday
        return !isHoliday(date, holidayType);
    }
    
    /**
     * Counts the number of holidays between two dates
     * @param startDate Start date
     * @param endDate End date
     * @param holidayType 1 for broker holidays, 2 for bank holidays
     * @return Number of holidays
     */
    public int getNumberOfHolidays(LocalDate startDate, LocalDate endDate, int holidayType) {
        LocalDate fromDate = startDate.isBefore(endDate) ? startDate : endDate;
        LocalDate toDate = startDate.isBefore(endDate) ? endDate : startDate;
        
        // Calculate weekend days
        long totalDays = java.time.temporal.ChronoUnit.DAYS.between(fromDate, toDate);
        long weekendDays = 0;
        
        LocalDate tempDate = fromDate;
        for (int i = 0; i <= totalDays; i++) {
            if (tempDate.getDayOfWeek() == DayOfWeek.SATURDAY || 
                tempDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
                weekendDays++;
            }
            tempDate = tempDate.plusDays(1);
        }
        
        // Get holidays excluding weekends
        int holidays = holidayRepository.countHolidaysBetween(fromDate, toDate, holidayType == 2);
        
        return (int) (weekendDays + holidays);
    }
}

/**
 * Interface for accessing holiday data
 */
interface HolidayRepository {
    boolean isHoliday(LocalDate date, boolean isBankHoliday);
    int countHolidaysBetween(LocalDate startDate, LocalDate endDate, boolean isBankHoliday);
}

/**
 * Example implementation using JPA
 */
class JpaHolidayRepository implements HolidayRepository {
    @Override
    public boolean isHoliday(LocalDate date, boolean isBankHoliday) {
        // Implementation would query the BROKERAGE_HOLIDAYS table
        // Example query:
        // SELECT COUNT(*) FROM BROKERAGE_HOLIDAYS 
        // WHERE HOLIDAY_DATE = :date 
        // AND (isBankHoliday ? BANK_HOL_FLAG : BROKER_HOL_FLAG) = 1
        return false; // Placeholder
    }
    
    @Override
    public int countHolidaysBetween(LocalDate startDate, LocalDate endDate, boolean isBankHoliday) {
        // Implementation would query the BROKERAGE_HOLIDAYS table
        // Example query:
        // SELECT COUNT(*) FROM BROKERAGE_HOLIDAYS 
        // WHERE HOLIDAY_DATE BETWEEN :startDate AND :endDate
        // AND (isBankHoliday ? BANK_HOL_FLAG : BROKER_HOL_FLAG) = 1
        return 0; // Placeholder
    }
}
