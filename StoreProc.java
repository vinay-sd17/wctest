import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Optional;

public class BusinessDayCalculator {
    private final HolidayRepository holidayRepository;
    
    public BusinessDayCalculator(HolidayRepository holidayRepository) {
        this.holidayRepository = holidayRepository;
    }
    
    public LocalDate getNextBusinessDay(LocalDate date, Integer numberOfDays, Integer holidayType) {
        LocalDate startDate = Optional.ofNullable(date).orElse(LocalDate.now());
        int daysToAdd = Optional.ofNullable(numberOfDays).orElse(1);
        int holidayFlag = Optional.ofNullable(holidayType).orElse(1);
        
        if (daysToAdd == 0) {
            return isBusinessDay(startDate, holidayFlag) ? 
                   startDate : 
                   getNextBusinessDay(startDate, 1, holidayFlag);
        }
        
        LocalDate tempDate = startDate;
        LocalDate newDate;
        int numOfDays = daysToAdd;
        
        do {
            // Calculate initial target date
            newDate = tempDate.plusDays(numOfDays);
            
            // Get number of holidays and weekends between dates
            int holidaysAndWeekends = getNumberOfHolidays(tempDate, newDate, holidayFlag);
            
            // If there are holidays/weekends, adjust the target date
            if (holidaysAndWeekends > 0) {
                tempDate = newDate;
                numOfDays = holidaysAndWeekends;
            } else {
                // No more holidays/weekends to skip
                break;
            }
        } while (true);
        
        // Final check to ensure the target date is a business day
        while (!isBusinessDay(newDate, holidayFlag)) {
            newDate = newDate.plusDays(1);
        }
        
        return newDate;
    }
    
    public int getNumberOfHolidays(LocalDate startDate, LocalDate endDate, int holidayType) {
        LocalDate fromDate = startDate.isBefore(endDate) ? startDate : endDate;
        LocalDate toDate = startDate.isBefore(endDate) ? endDate : startDate;
        
        // Calculate weekends
        int weekendDays = 0;
        LocalDate current = fromDate;
        while (!current.isAfter(toDate)) {
            if (current.getDayOfWeek() == DayOfWeek.SATURDAY || 
                current.getDayOfWeek() == DayOfWeek.SUNDAY) {
                weekendDays++;
            }
            current = current.plusDays(1);
        }
        
        // Get holidays excluding weekends
        int holidays = holidayRepository.countHolidaysBetween(fromDate, toDate, holidayType == 2);
        
        return weekendDays + holidays;
    }
    
    private boolean isBusinessDay(LocalDate date, int holidayType) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            return false;
        }
        return !holidayRepository.isHoliday(date, holidayType == 2);
    }
}
