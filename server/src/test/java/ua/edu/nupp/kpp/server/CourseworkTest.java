package ua.edu.nupp.kpp.server;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Модульні тести предметної логіки (Етап IV).
 * Не потребують MySQL, Tomcat чи JavaFX.
 * Усього 15 тестів.
 */
public class CourseworkTest {

    // ========== RiskCalculator / нормування ==========

    @Test
    void normalizeParticipantsZero() {
        assertEquals(0.0, RiskCalculator.normalizeParticipants(0));
    }

    @Test
    void normalizeParticipantsHalf() {
        assertEquals(50.0, RiskCalculator.normalizeParticipants(25));
    }

    @Test
    void normalizeParticipantsMax() {
        assertEquals(100.0, RiskCalculator.normalizeParticipants(50));
    }

    @Test
    void normalizeParticipantsAboveMaxCapped() {
        assertEquals(100.0, RiskCalculator.normalizeParticipants(80));
    }

    @Test
    void normalizeDurationHalf() {
        // 90 хв / 180 * 100 = 50
        assertEquals(50.0, RiskCalculator.normalizeDuration(90));
    }

    @Test
    void normalizeDurationMax() {
        assertEquals(100.0, RiskCalculator.normalizeDuration(180));
    }

    @Test
    void calculateIntegralIndex() {
        // 0.40*50 + 0.35*60 + 0.25*40 = 20 + 21 + 10 = 51.0
        assertEquals(51.0, RiskCalculator.calculate(50, 60, 40));
    }

    @Test
    void calculateAnotherExample() {
        // 0.40*30 + 0.35*40 + 0.25*20 = 12 + 14 + 5 = 31.0
        assertEquals(31.0, RiskCalculator.calculate(30, 40, 20));
    }

    @Test
    void riskRejectsFactorAbove100() {
        assertThrows(IllegalArgumentException.class,
                () -> RiskCalculator.calculate(101, 10, 10));
    }

    @Test
    void riskRejectsNegativeFactor() {
        assertThrows(IllegalArgumentException.class,
                () -> RiskCalculator.calculate(-1, 10, 10));
    }

    // ========== Lifecycle ==========

    @Test
    void createdToConfirmedAllowed() {
        assertTrue(BookingService.isAllowedTransition(BookingState.CREATED, BookingState.CONFIRMED));
    }

    @Test
    void confirmedToActiveAllowed() {
        assertTrue(BookingService.isAllowedTransition(BookingState.CONFIRMED, BookingState.ACTIVE));
    }

    @Test
    void activeToCompletedAllowed() {
        assertTrue(BookingService.isAllowedTransition(BookingState.ACTIVE, BookingState.COMPLETED));
    }

    @Test
    void createdToCompletedRejected() {
        assertFalse(BookingService.isAllowedTransition(BookingState.CREATED, BookingState.COMPLETED));
    }

    @Test
    void completedToAnythingRejected() {
        assertFalse(BookingService.isAllowedTransition(BookingState.COMPLETED, BookingState.ACTIVE));
        assertFalse(BookingService.isAllowedTransition(BookingState.COMPLETED, BookingState.CREATED));
    }

    // ========== Validation ==========

    @Test
    void facilityValidationWorks() {
        assertThrows(IllegalArgumentException.class,
                () -> FacilityService.validate(new Facility(null, "", "Назва", "Адреса")));
    }

    @Test
    void userValidationWorks() {
        assertThrows(IllegalArgumentException.class,
                () -> AppUserService.validate(new AppUser(null, "Ім’я", "bad-email", "123")));
    }

    @Test
    void timeSlotValidationWorks() {
        assertThrows(IllegalArgumentException.class,
                () -> TimeSlotService.validate(new TimeSlot(null, 0, "2026-11-01", "10:00", "12:00", 100)));
    }
}
