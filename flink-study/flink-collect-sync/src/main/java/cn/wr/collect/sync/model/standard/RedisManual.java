package cn.wr.collect.sync.model.standard;

import lombok.Data;

import java.io.Serializable;

@Data
public class RedisManual implements Serializable {
    private static final long serialVersionUID = 4395035165726453659L;

    private String id;
    private String approvalNumber;
    private String commonName;
    private String enName;
    private String pinyinName;
    private String taboo;
    private String interaction;
    private String composition;
    private String pharmacologicalEffects;
    private String dosage;
    private String clinicalClassification;
    private String cureDisease;
    private String attentions;
    private String manufacturer;
    private String specification;
    private String storage;
    private String pediatricUse;
    private String geriatricUse;
    private String pregnancyAndNursingMothers;
    private String overDosage;
    private String validity;
    private String drugName;
    private String relativeSickness;
    private String prescriptionType;
    private String indications;
    private String drugType;
    private String packaging;
    private String sideEffect;
    private String hint;
    private String attrPtitle;
    private String attrCtitle;
    private String pharmacokinetics;
    private RedisMutex mutex;

}
