package com.reportatucalle.modules.domain;

import com.reportatucalle.modules.auth.domain.entity.AuthAccount;
import com.reportatucalle.modules.auth.domain.entity.Role;
import com.reportatucalle.modules.category.domain.entity.AlgorithmType;
import com.reportatucalle.modules.category.domain.entity.Category;
import com.reportatucalle.modules.media.domain.entity.Media;
import com.reportatucalle.modules.report.domain.entity.Report;
import com.reportatucalle.modules.report.domain.entity.ReportStatus;
import com.reportatucalle.modules.user.domain.entity.UserProfile;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;

import static org.junit.jupiter.api.Assertions.*;

class DomainLogicTest {

    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    @Test
    void authAccount_validatesEmailPasswordAndAuthority() {
        AuthAccount valid = AuthAccount.builder()
                .email("ciudadano@mail.com").passwordHash("hash").role(Role.CITIZEN).build();
        AuthAccount invalid = AuthAccount.builder()
                .email("mal").passwordHash(" ").role(Role.ADMIN).build();

        assertTrue(valid.isEmailValid());
        assertTrue(valid.canAuthenticate());
        assertTrue(valid.isPasswordValid("secreto", "encoder"));
        assertEquals("ROLE_CITIZEN", valid.getAuthorityName());
        assertFalse(invalid.isEmailValid());
        assertFalse(invalid.canAuthenticate());
        assertFalse(invalid.isPasswordValid(" ", "encoder"));
    }

    @Test
    void category_algorithmHelpersWork() {
        assertTrue(category(AlgorithmType.ROUTING, true).requiresRouting());
        assertTrue(category(AlgorithmType.FLOW, true).requiresFlow());
        assertTrue(category(AlgorithmType.CONNECTIVITY, true).requiresConnectivity());
        assertTrue(category(AlgorithmType.NONE, true).requiresNoAlgorithm());
        assertTrue(category(AlgorithmType.NONE, true).isAvailable());
        assertFalse(category(AlgorithmType.NONE, false).isAvailable());
        assertFalse(category(AlgorithmType.NONE, null).isAvailable());
    }

    @Test
    void media_validatesFileMetadataAndExtension() {
        Media image = Media.builder()
                .fileName("foto.png").fileUrl("http://localhost/foto.png").contentType("image/png")
                .fileSizeBytes(20L).uploadedByCitizenId(1L).build();
        Media invalid = Media.builder().fileName(" ").fileUrl(null).contentType("text/plain").fileSizeBytes(0L).build();

        assertTrue(image.isValid());
        assertTrue(image.isImage());
        assertEquals("png", image.getFileExtensionFromUrl());
        assertFalse(invalid.isValid());
        assertFalse(invalid.isImage());
        assertEquals("", Media.builder().fileUrl(null).build().getFileExtensionFromUrl());
        assertEquals("", Media.builder().fileUrl("sinextension").build().getFileExtensionFromUrl());
    }

    @Test
    void userProfile_formatsAndValidatesProfile() {
        UserProfile profile = UserProfile.builder()
                .id(1L).accountId(5L).firstName("Ana").lastName("Torres").phone("+51999999999").build();
        UserProfile incomplete = UserProfile.builder().firstName(" ").lastName("Torres").phone("abc").build();

        assertEquals("Ana Torres", profile.getFullName());
        assertTrue(profile.isComplete());
        assertTrue(profile.isPhoneValid());
        assertFalse(incomplete.isComplete());
        assertFalse(incomplete.isPhoneValid());
        assertEquals("Solo", UserProfile.builder().firstName("Solo").lastName(null).build().getFullName());
    }

    @Test
    void report_validatesStatusSeverityAndBuilderRequirements() {
        Report pending = report(ReportStatus.PENDING, 1);
        Report inProgress = report(ReportStatus.IN_PROGRESS, 2);
        Report resolved = report(ReportStatus.RESOLVED, 3);
        Report critical = report(ReportStatus.REJECTED, 5);

        assertTrue(pending.isValid());
        assertTrue(pending.isActive());
        assertTrue(inProgress.isActive());
        assertFalse(resolved.isActive());
        assertFalse(resolved.canUpdate());
        assertEquals("BAJA", pending.getSeverity());
        assertEquals("MEDIA", inProgress.getSeverity());
        assertEquals("ALTA", resolved.getSeverity());
        assertEquals("CRITICA", critical.getSeverity());
        assertEquals(1, Report.builder().citizenId(1L).categoryId(1L).title("T").description("D")
                .location(geometryFactory.createPoint(new Coordinate(-77, -12))).reportCount(null).build().getReportCount());

        assertThrows(IllegalArgumentException.class, () -> Report.builder().categoryId(1L).title("T").description("D")
                .location(geometryFactory.createPoint(new Coordinate(-77, -12))).build());
        assertThrows(IllegalArgumentException.class, () -> Report.builder().citizenId(1L).title("T").description("D")
                .location(geometryFactory.createPoint(new Coordinate(-77, -12))).build());
        assertThrows(IllegalArgumentException.class, () -> Report.builder().citizenId(1L).categoryId(1L).title(" ").description("D")
                .location(geometryFactory.createPoint(new Coordinate(-77, -12))).build());
        assertThrows(IllegalArgumentException.class, () -> Report.builder().citizenId(1L).categoryId(1L).title("T").description(" ")
                .location(geometryFactory.createPoint(new Coordinate(-77, -12))).build());
        assertThrows(IllegalArgumentException.class, () -> Report.builder().citizenId(1L).categoryId(1L).title("T").description("D").build());
    }

    private Category category(AlgorithmType type, Boolean active) {
        return Category.builder().name("Cat").description("Desc").markerColor("#000000")
                .algorithmType(type).isActive(active).build();
    }

    private Report report(ReportStatus status, int count) {
        return Report.builder().citizenId(1L).categoryId(1L).title("Título").description("Descripción")
                .location(geometryFactory.createPoint(new Coordinate(-77.0, -12.0)))
                .status(status).reportCount(count).build();
    }
}
