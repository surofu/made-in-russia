package com.surofu.exporteru.core.service.deliveryTerm;

import com.surofu.exporteru.core.service.deliveryTerm.operation.DeleteDeliveryTerm;
import com.surofu.exporteru.core.service.deliveryTerm.operation.GetAllDeliveryTerms;
import com.surofu.exporteru.core.service.deliveryTerm.operation.SaveDeliveryTerm;

public interface DeliveryTermService {
  GetAllDeliveryTerms.Result getAll();

  SaveDeliveryTerm.Result save(SaveDeliveryTerm operation);

  DeleteDeliveryTerm.Result delete(DeleteDeliveryTerm operation);
}
