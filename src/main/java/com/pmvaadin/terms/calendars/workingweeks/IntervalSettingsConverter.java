package com.pmvaadin.terms.calendars.workingweeks;

import org.springframework.stereotype.Component;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Component
@Converter(autoApply = true)
public class IntervalSettingsConverter implements AttributeConverter<IntervalSetting, Integer> {

    @Override
    public Integer convertToDatabaseColumn(IntervalSetting intervalSetting) {
        if (intervalSetting == null) {
            return null;
        }
        return intervalSetting.getCode();
    }

    @Override
    public IntervalSetting convertToEntityAttribute(Integer code) {
        if (code == null) {
            return null;
        }

        return IntervalSetting.of(code);

//        return Stream.of(IntervalSettings.values())
//                .filter(c -> c.getCode().equals(code))
//                .findFirst()
//                .orElseThrow(IllegalArgumentException::new);
    }

}
