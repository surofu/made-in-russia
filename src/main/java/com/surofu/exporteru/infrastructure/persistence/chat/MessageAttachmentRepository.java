package com.surofu.exporteru.infrastructure.persistence.chat;

import com.surofu.exporteru.core.model.chat.MessageAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;




@Repository
public interface MessageAttachmentRepository extends JpaRepository<MessageAttachment, Long> {

}