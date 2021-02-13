package com.pjm.common.entity;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;

@Data
@Document(collection = "user")
public class PjmCloudUserLbsUser implements Serializable {

    @Id
    private ObjectId id;

    private String name;

    private String address;

    private String gender;

    private List<Double> loc;
}