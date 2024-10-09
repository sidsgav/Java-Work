package com.nea.patient.access.portal.ui.application;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JFormattedTextField.AbstractFormatter;

import org.apache.commons.lang3.StringUtils;

public class DateWidgetTextFormatter extends AbstractFormatter {

  private static final long serialVersionUID = 7140868377210227295L;

  private static final String DATE_PATTERN = "dd-MM-yyyy";
  private SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_PATTERN);

  @Override
  public Object stringToValue(final String text) throws ParseException {
    return dateFormatter.parseObject(text);
  }

  @Override
  public String valueToString(final Object value) throws ParseException {
    if (value != null) {
      Calendar calendar = (Calendar) value;
      return dateFormatter.format(calendar.getTime());
    }
    return StringUtils.EMPTY;
  }
}
