package com.surofu.madeinrussia.application.service;

import com.surofu.madeinrussia.application.dto.DeliveryMethodDto;
import com.surofu.madeinrussia.core.model.deliveryMethod.DeliveryMethod;
import com.surofu.madeinrussia.core.model.deliveryMethod.DeliveryMethodCreationDate;
import com.surofu.madeinrussia.core.model.deliveryMethod.DeliveryMethodLastModificationDate;
import com.surofu.madeinrussia.core.model.deliveryMethod.DeliveryMethodName;
import com.surofu.madeinrussia.core.repository.DeliveryMethodRepository;
import com.surofu.madeinrussia.core.service.deliveryMethod.operation.GetDeliveryMethodById;
import com.surofu.madeinrussia.core.service.deliveryMethod.operation.GetDeliveryMethods;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryMethodApplicationServiceTest {

    ZonedDateTime TEST_DATE_TIME = ZonedDateTime.now();

    @Mock
    DeliveryMethodRepository deliveryMethodRepository;

    @InjectMocks
    DeliveryMethodApplicationService deliveryMethodApplicationService;

    @Test
    void getDeliveryMethods_ReturnSuccessResultWithNotEmptyDeliveryMethodList() {
        // given
        List<DeliveryMethod> mockDeliveryMethods = new ArrayList<>();
        List<DeliveryMethodDto> mockDeliveryMethodDtos = new ArrayList<>();

        int deliveryMethodCount = 5;

        IntStream.range(0, deliveryMethodCount).forEach(i -> {
            DeliveryMethod deliveryMethod = new DeliveryMethod();
            deliveryMethod.setId((long) i);
            deliveryMethod.setName(DeliveryMethodName.of(String.format("Delivery Method %s", i)));
            deliveryMethod.setCreationDate(DeliveryMethodCreationDate.of(TEST_DATE_TIME));
            deliveryMethod.setLastModificationDate(DeliveryMethodLastModificationDate.of(TEST_DATE_TIME));

            mockDeliveryMethods.add(deliveryMethod);
            mockDeliveryMethodDtos.add(DeliveryMethodDto.of(deliveryMethod));
        });

        doReturn(mockDeliveryMethods).when(deliveryMethodRepository).getAllDeliveryMethods();

        // when
        GetDeliveryMethods.Result getDeliveryMethodsResult = deliveryMethodApplicationService.getDeliveryMethods();

        // then
        assertNotNull(getDeliveryMethodsResult);
        assertInstanceOf(GetDeliveryMethods.Result.class, getDeliveryMethodsResult);
        assertInstanceOf(GetDeliveryMethods.Result.Success.class, getDeliveryMethodsResult);

        GetDeliveryMethods.Result.Success getDeliveryMethodsResultSuccess = (GetDeliveryMethods.Result.Success) getDeliveryMethodsResult;
        List<DeliveryMethodDto> resultDeliveryMethods = getDeliveryMethodsResultSuccess.getDeliveryMethodDtos();

        assertNotNull(resultDeliveryMethods);
        assertEquals(deliveryMethodCount, resultDeliveryMethods.size());

        IntStream.range(0, deliveryMethodCount).forEach(i -> {
            assertNotNull(resultDeliveryMethods.get(i));
            assertEquals(mockDeliveryMethodDtos.get(i).getId(), resultDeliveryMethods.get(i).getId());
            assertEquals(mockDeliveryMethodDtos.get(i).getName(), resultDeliveryMethods.get(i).getName());
            assertEquals(mockDeliveryMethodDtos.get(i).getCreationDate(), resultDeliveryMethods.get(i).getCreationDate());
            assertEquals(mockDeliveryMethodDtos.get(i).getLastModificationDate(), resultDeliveryMethods.get(i).getLastModificationDate());
        });

        verify(deliveryMethodRepository, times(1)).getAllDeliveryMethods();
    }

    @Test
    void getDeliveryMethodById_WhenDeliveryMethodExistsById_ReturnSuccessResultWithValidDeliveryMethodDto() {
        // given
        long mockDeliveryMethodId = 1L;

        DeliveryMethod mockDeliveryMethod = new DeliveryMethod();
        mockDeliveryMethod.setId(mockDeliveryMethodId);
        mockDeliveryMethod.setName(DeliveryMethodName.of(String.format("Delivery Method %s", mockDeliveryMethodId)));
        mockDeliveryMethod.setCreationDate(DeliveryMethodCreationDate.of(TEST_DATE_TIME));
        mockDeliveryMethod.setLastModificationDate(DeliveryMethodLastModificationDate.of(TEST_DATE_TIME));

        DeliveryMethodDto mockDeliveryMethodDto = DeliveryMethodDto.of(mockDeliveryMethod);

        doReturn(Optional.of(mockDeliveryMethod)).when(deliveryMethodRepository).getDeliveryMethodById(mockDeliveryMethodId);

        // when
        GetDeliveryMethodById getDeliveryMethodByIdOperation = GetDeliveryMethodById.of(mockDeliveryMethodId);
        GetDeliveryMethodById.Result getDeliveryMethodByIdResult = deliveryMethodApplicationService.getDeliveryMethodById(getDeliveryMethodByIdOperation);

        // then
        assertNotNull(getDeliveryMethodByIdResult);
        assertInstanceOf(GetDeliveryMethodById.Result.class, getDeliveryMethodByIdResult);
        assertInstanceOf(GetDeliveryMethodById.Result.Success.class, getDeliveryMethodByIdResult);

        GetDeliveryMethodById.Result.Success getDeliveryMethodByIdResultSuccess = (GetDeliveryMethodById.Result.Success) getDeliveryMethodByIdResult;
        DeliveryMethodDto deliveryMethodDtoResult = getDeliveryMethodByIdResultSuccess.getDeliveryMethodDto();

        assertNotNull(deliveryMethodDtoResult);
        assertEquals(mockDeliveryMethodDto.getId(), deliveryMethodDtoResult.getId());
        assertEquals(mockDeliveryMethodDto.getName(), deliveryMethodDtoResult.getName());
        assertEquals(mockDeliveryMethodDto.getCreationDate(), deliveryMethodDtoResult.getCreationDate());
        assertEquals(mockDeliveryMethodDto.getLastModificationDate(), deliveryMethodDtoResult.getLastModificationDate());

        verify(deliveryMethodRepository, times(1)).getDeliveryMethodById(mockDeliveryMethodId);
    }

    @Test
    void getDeliveryMethodById_WhenDeliveryMethodDoesNotExistById_ReturnsNotFoundResult() {
        // given
        doReturn(Optional.empty()).when(deliveryMethodRepository).getDeliveryMethodById(anyLong());

        // when
        long deliveryMethodIdToFind = 1L;
        GetDeliveryMethodById getDeliveryMethodByIdOperation = GetDeliveryMethodById.of(deliveryMethodIdToFind);
        GetDeliveryMethodById.Result getDeliveryMethodByIdResult = deliveryMethodApplicationService.getDeliveryMethodById(getDeliveryMethodByIdOperation);

        // then
        assertNotNull(getDeliveryMethodByIdResult);
        assertInstanceOf(GetDeliveryMethodById.Result.class, getDeliveryMethodByIdResult);
        assertInstanceOf(GetDeliveryMethodById.Result.NotFound.class, getDeliveryMethodByIdResult);

        GetDeliveryMethodById.Result.NotFound getDeliveryMethodByIdResultNotFound = (GetDeliveryMethodById.Result.NotFound) getDeliveryMethodByIdResult;
        Long resultDeliveryMethodId = getDeliveryMethodByIdResultNotFound.getDeliveryMethodId();

        assertNotNull(resultDeliveryMethodId);
        assertEquals(deliveryMethodIdToFind, resultDeliveryMethodId);

        verify(deliveryMethodRepository, times(1)).getDeliveryMethodById(deliveryMethodIdToFind);
    }
}