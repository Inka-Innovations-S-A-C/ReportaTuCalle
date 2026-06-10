package com.reportatucalle.modules.persistence;

import com.reportatucalle.modules.auth.domain.entity.AuthAccount;
import com.reportatucalle.modules.auth.domain.entity.Role;
import com.reportatucalle.modules.auth.infrastructure.persistence.entity.AuthAccountJpaEntity;
import com.reportatucalle.modules.auth.infrastructure.persistence.mapper.AuthPersistenceMapper;
import com.reportatucalle.modules.category.domain.entity.AlgorithmType;
import com.reportatucalle.modules.category.domain.entity.Category;
import com.reportatucalle.modules.category.infrastructure.persistence.entity.CategoryJpaEntity;
import com.reportatucalle.modules.category.infrastructure.persistence.mapper.CategoryMapper;
import com.reportatucalle.modules.report.domain.entity.Report;
import com.reportatucalle.modules.report.domain.entity.ReportStatus;
import com.reportatucalle.modules.report.infrastructure.persistence.entity.ReportJpaEntity;
import com.reportatucalle.modules.report.infrastructure.persistence.mapper.ReportPersistenceMapper;
import com.reportatucalle.modules.user.domain.entity.UserProfile;
import com.reportatucalle.modules.user.infrastructure.persistence.entity.UserProfileJpaEntity;
import com.reportatucalle.modules.user.infrastructure.persistence.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PersistenceMapperTest {

    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    @Test
    void authPersistenceMapper_mapsBothWaysAndHandlesNulls() {
        AuthPersistenceMapper mapper = new AuthPersistenceMapper();
        LocalDateTime now = LocalDateTime.now();
        AuthAccountJpaEntity jpa = AuthAccountJpaEntity.builder()
                .id(1L).email("a@mail.com").password("hash").role(Role.ADMIN).createdAt(now).build();

        AuthAccount domain = mapper.toDomain(jpa);
        AuthAccountJpaEntity back = mapper.toJpaEntity(domain);
        AuthAccountJpaEntity creation = mapper.toJpaEntityForCreation(domain);

        assertEquals("a@mail.com", domain.getEmail());
        assertEquals(Role.ADMIN, back.getRole());
        assertNull(creation.getId());
        assertNull(mapper.toDomain(null));
        assertNull(mapper.toJpaEntity(null));
        assertNull(mapper.toJpaEntityForCreation(null));
    }

    @Test
    void categoryMapper_mapsBothWaysAndHandlesNulls() {
        CategoryMapper mapper = new CategoryMapper();
        LocalDateTime now = LocalDateTime.now();
        CategoryJpaEntity jpa = CategoryJpaEntity.builder()
                .id(2L).name("Bache").description("Huecos").markerColor("#FF0000")
                .algorithmType("ROUTING").isActive(true).createdAt(now).updatedAt(now).build();

        Category domain = mapper.toDomain(jpa);
        CategoryJpaEntity back = mapper.toJpaEntity(domain);

        assertEquals(AlgorithmType.ROUTING, domain.getAlgorithmType());
        assertEquals("ROUTING", back.getAlgorithmType());
        assertNull(mapper.toDomain(null));
        assertNull(mapper.toJpaEntity(null));
    }

    @Test
    void reportPersistenceMapper_mapsBothWaysAndHandlesNulls() {
        ReportPersistenceMapper mapper = new ReportPersistenceMapper();
        LocalDateTime now = LocalDateTime.now();
        var point = geometryFactory.createPoint(new Coordinate(-77, -12));
        ReportJpaEntity jpa = ReportJpaEntity.builder()
                .id(3L).citizenId(4L).categoryId(5L).title("T").description("D")
                .imageUrl("img").location(point).status(ReportStatus.PENDING)
                .reportCount(2).createdAt(now).updatedAt(now).build();

        Report domain = mapper.toDomain(jpa);
        ReportJpaEntity back = mapper.toJpaEntity(domain);
        ReportJpaEntity creation = mapper.toJpaEntityForCreation(domain);

        assertEquals(4L, domain.getCitizenId());
        assertEquals(ReportStatus.PENDING, back.getStatus());
        assertNull(creation.getId());
        assertNull(mapper.toDomain(null));
        assertNull(mapper.toJpaEntity(null));
        assertNull(mapper.toJpaEntityForCreation(null));
    }

    @Test
    void userMapper_mapsBothWaysAndHandlesNulls() {
        UserMapper mapper = new UserMapper();
        LocalDateTime now = LocalDateTime.now();
        UserProfileJpaEntity jpa = UserProfileJpaEntity.builder()
                .id(6L).accountId(7L).firstName("Ana").lastName("Torres").phone("999999999").createdAt(now).build();

        UserProfile domain = mapper.toDomain(jpa);
        UserProfileJpaEntity back = mapper.toJpaEntity(domain);
        UserProfileJpaEntity creation = mapper.toJpaEntityForCreation(domain);

        assertEquals("Ana Torres", domain.getFullName());
        assertEquals(7L, back.getAccountId());
        assertNull(creation.getId());
        assertNull(mapper.toDomain(null));
        assertNull(mapper.toJpaEntity(null));
        assertNull(mapper.toJpaEntityForCreation(null));
    }
}
