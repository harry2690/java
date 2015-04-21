package com.harrywu.springweb.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileCleaningTracker;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import com.harrywu.springweb.common.impl.CustomEntityImpl;

public class CommonUtils {
    
    public enum DATE_TYPE {TO_DAY, NONE, TOMORROW, YESTERDAY};
    public enum DATE_FIELD_TYPE {STRING, DATE};

    private static final Log log = LogFactory.getLog(CommonUtils.class);
            
    public static String customResultToJSONString(CustomResult<?> customResult) {
        JSONObject json = new JSONObject();
        try {
            json.put("isSuccess", customResult.isSuccess());
            if (customResult.getResult() instanceof Entity) {
                Map<String, Object> entityMap = ((Entity) customResult.getResult()).getValuedProperties();
                JSONObject entityJson = new JSONObject(entityMap);
                json.put("returnMessage", entityJson.toString());                
            } else {
                json.put("returnMessage", customResult.getResult());
            }
            return json.toString();
        } catch (JSONException e) {
            return "{isSuccess: false, returnMessage: \"" + e.getLocalizedMessage() + "\"}";
        }
    }
    
    public static String resultMessageToJSONString(boolean isSuccess, String returnMessage) {
        JSONObject json = new JSONObject();
        try {
            json.put("isSuccess", isSuccess);
            json.put("returnMessage", returnMessage);
            return json.toString();
        } catch (JSONException e) {
            return "{isSuccess: false, returnMessage: \"" + e.getLocalizedMessage() + "\"}";
        }
    }

    @SuppressWarnings("unchecked")
    public static CustomEntity parsingCustomerReportData(String customerReportDataStr) throws Exception {
        try {
            JSONObject json = new JSONObject(customerReportDataStr);
            JSONObject content = null;
            switch (json.getInt("contentType")) {
                case 0: // JSON String
                    content = json.getJSONObject("content");
                    break;
                case 1: // XMLS
                    content = XML.toJSONObject(json.getString("content"));
                    break;
                default: // String
                    content = new JSONObject();
                    content.put("content", json.getString("content"));
                    break;
            }
            if (content != null) {
                CustomEntity customerReportData = new CustomEntityImpl();
                Iterator<String> keys = content.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    customerReportData.setProperty(key, content.getString(key)); 
                }
                return customerReportData;
            }
        } catch (JSONException e) {
            String str = "format: {contentType: 0, content:\"\"}";
            throw new Exception(e.getLocalizedMessage() + ", " + str);
        }
        return null;
    }
    
    public static JSONObject entityToJSONObject(Entity entity) {
        Map<String, Object> valuedProperties = entity.getValuedProperties();
        return new JSONObject(valuedProperties);
    }
    
    @SuppressWarnings("unchecked")
    public static Entity JSONObjectToEntity(JSONObject json, Class<Entity> clazz) {
        try {
            Entity result = clazz.newInstance();
            Iterator<String> keys = json.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                Object value = json.get(key);
                result.setProperty(key, value);
            }
            return result;
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    /**
     * inputDatePattern = outputDatePattern = yyyyMMdd
     */
    public static String genDateQueryString(DATE_TYPE dateType, DATE_FIELD_TYPE dateFieldType, String dateFieldName,
                                            String dateFieldNameFrom, String dateFieldNameTo, 
                                            Map<String, Object> entity) throws Exception {
        return genDateQueryString(dateType, dateFieldType, dateFieldName, dateFieldNameFrom, dateFieldNameTo, 
                                  entity, "yyyyMMdd");
    }

    public static String genDateQueryString(DATE_TYPE dateType, DATE_FIELD_TYPE dateFieldType, String dateFieldName,
                                            String dateFieldNameFrom, String dateFieldNameTo, 
                                            Map<String, Object> entity, String inputDatePattern) throws Exception {
        return genDateQueryString(dateType, dateFieldType, dateFieldName, dateFieldNameFrom, dateFieldNameTo, 
                                  entity, inputDatePattern,
                                  SimpleDateFormat.class);
    }

    public static <Clazz extends SimpleDateFormat> String genDateQueryString(
                                    DATE_TYPE dateType, DATE_FIELD_TYPE dateFieldType, String dateFieldName,
                                    String dateFieldNameFrom, String dateFieldNameTo, 
                                    Map<String, Object> entity, String inputDatePattern,
                                    Class<Clazz> formatterClass) throws Exception {
        return genDateQueryString(dateType, dateFieldType, dateFieldName, dateFieldNameFrom, dateFieldNameTo, entity,
                                  inputDatePattern, inputDatePattern,
                                  formatterClass);
    }

    public static <Clazz extends SimpleDateFormat> String genDateQueryString(
                                DATE_TYPE dateType, DATE_FIELD_TYPE dateFieldType,
                                String dateFieldName, String dateFieldNameFrom, String dateFieldNameTo,
                                Map<String, Object> entity,
                                String inputDatePattern, String outputDatePattern,
                                Class<Clazz> formatterClass) throws Exception {
        return genDateQueryString(null, dateType, dateFieldType, dateFieldName, dateFieldNameFrom, dateFieldNameTo, entity,
                                  inputDatePattern, outputDatePattern,
                                  formatterClass);
    }

    /**
     * @param dateType: TO_DAY or NONE(Range)
     * @param dateFieldType: STRING/DATE 
     * @param dateFieldName: Table field name
     * @param dateFieldNameFrom: Query condition fieldName, not table field name
     * @param dateFieldNameTo: Query condition fieldName, not table field name
     * @param entity: values
     * @param inputDatePattern: values 鋆∠��交��澆�
     * @param outputDatePattern: ���霈�SQL��撘�
     * @param formatterClass: SimpleDateFomat/ROCDateFormat
     * @return where SQL string
     * @throws Exception
     * sample:
     * input 2011/01/22 15:15:23 --> output 20110122151523
     *   inputDatePattern: yyyy/MM/dd HH:mm:ss
     *   outputDatePattern: yyyyMMddHHmmss
     */
    public static <Clazz extends SimpleDateFormat> String genDateQueryString(
                                String alias,
                                DATE_TYPE dateType, DATE_FIELD_TYPE dateFieldType,
                                String dateFieldName, String dateFieldNameFrom, String dateFieldNameTo,
                                Map<String, Object> entity,
                                String inputDatePattern, String outputDatePattern,
                                Class<Clazz> formatterClass) throws Exception {
        SimpleDateFormat inputFieldDateFormat = formatterClass.newInstance();
        inputFieldDateFormat.applyPattern(inputDatePattern);
        SimpleDateFormat outputFieldDateFormat = formatterClass.newInstance();
        outputFieldDateFormat.applyPattern(outputDatePattern);
        // �折頧���
        String dateFormatString = "yyyy/MM/dd";
        SimpleDateFormat dateFormat = formatterClass.newInstance();
        dateFormat.applyPattern(dateFormatString);

        String dateTimeFormatString = "yyyy/MM/dd HH:mm:ss";
        SimpleDateFormat dateTimeFormat = formatterClass.newInstance();
        dateTimeFormat.applyPattern(dateTimeFormatString);
        switch (dateType) {
            case TO_DAY:
                Date today = Calendar.getInstance().getTime();
                String todayStr = dateFormat.format(today);

                updateDateValues(dateFieldType, dateFieldNameFrom, dateFieldNameTo, entity,
                                 inputFieldDateFormat,
                                 dateTimeFormat.parse(todayStr + " 00:00:00"),
                                 dateTimeFormat.parse(todayStr + " 23:59:59"));
                if (alias != null)
                    return alias + "." + dateFieldName + " between :" + dateFieldNameFrom + " and :" + dateFieldNameTo;
                return dateFieldName + " between :" + dateFieldNameFrom + " and :" + dateFieldNameTo;
            default:
                boolean dateFromExist = entity.containsKey(dateFieldNameFrom);
                boolean dateToExist = entity.containsKey(dateFieldNameTo);
                if (dateFromExist || dateToExist) {
                    Object dateFromObj = entity.get(dateFieldNameFrom);
                    Object dateToObj = entity.get(dateFieldNameTo);

                    if (dateFromExist && dateToExist) {
                        Date dateFrom = null;
                        Date dateTo = null;
                        if (dateFromObj instanceof Date)
                            dateFrom = (Date) dateFromObj;
                        else
                            dateFrom = inputFieldDateFormat.parse(dateFromObj.toString());
                        if (dateToObj instanceof Date)
                            dateTo = (Date) dateToObj;
                        else
                            dateTo = inputFieldDateFormat.parse(dateToObj.toString());

                        // dateForm > dateTo ==> exchange
                        if (dateFrom.after(dateTo)) {
                            Date tmp = dateFrom;
                            dateFrom = dateTo;
                            dateTo = tmp;
                        }

                        String dateFromStr = dateFormat.format(dateFrom);
                        String dateToStr = dateFormat.format(dateTo);

                        updateDateValues(dateFieldType, dateFieldNameFrom, dateFieldNameTo, entity,
                                         outputFieldDateFormat,
                                         dateTimeFormat.parse(dateFromStr + " 00:00:00"),
                                         dateTimeFormat.parse(dateToStr + " 23:59:59"));

                        if (alias != null)
                            return alias + "." + dateFieldName + " between :" + dateFieldNameFrom + " and :" + dateFieldNameTo;
                        return dateFieldName + " between :" + dateFieldNameFrom + " and :" + dateFieldNameTo;
                    } else
                        if (dateFromExist && !dateToExist) {
                            Date dateFrom = null;
                            if (dateFromObj instanceof Date)
                                dateFrom = (Date) dateFromObj;
                            else
                                dateFrom = inputFieldDateFormat.parse(dateFromObj.toString());

                            String dateFromStr = dateFormat.format(dateFrom);

                            updateDateValues(dateFieldType, dateFieldNameFrom, dateFieldNameTo, entity,
                                             outputFieldDateFormat,
                                             dateTimeFormat.parse(dateFromStr + " 00:00:00"), null);

                            if (alias != null)
                                return alias + "." + dateFieldName + " >= :" + dateFieldNameFrom;
                            return dateFieldName + " >= :" + dateFieldNameFrom;
                        } else
                            if (!dateFromExist && dateToExist) {
                                Date dateTo = null;
                                if (dateToObj instanceof Date)
                                    dateTo = (Date) dateToObj;
                                else
                                    dateTo = inputFieldDateFormat.parse(dateToObj.toString());

                                String dateToStr = dateFormat.format(dateTo);

                                updateDateValues(dateFieldType, dateFieldNameFrom, dateFieldNameTo, entity,
                                                 outputFieldDateFormat,
                                                 null, dateTimeFormat.parse(dateToStr + " 23:59:59"));

                                if (alias != null)
                                    return alias + "." + dateFieldName + " <= :" + dateFieldNameTo;
                                return dateFieldName + " <= :" + dateFieldNameTo;
                            }
                }
                return null;
        }
    }

    public static String genLikeFieldQueryString(String fieldName, Entity entity) throws Exception {
        Map<String, Object> values = entity.getValuedProperties();
        String sql = genLikeFieldQueryString(fieldName, values);
        entity.readValuesFromMap(values);
        return sql;
    }

    public static String genLikeFieldQueryString(String fieldName, Map<String, Object> entity) throws Exception {
        return genLikeFieldQueryString(fieldName, entity, "*");
    }

    public static String genLikeFieldQueryString(String fieldName, Map<String, Object> entity, String wildcard) throws Exception {
        return genLikeFieldQueryString(null, fieldName, entity, wildcard);
    }

    /*
     * replace value from wildcard('abc*') --> like('abc%')
     */
    public static String genLikeFieldQueryString(String alias, String fieldName, Map<String, Object> entity, String wildcard) throws Exception {
        if (entity.containsKey(fieldName)) {
            Object fieldValueObj = entity.get(fieldName);
            if (fieldValueObj != null && fieldValueObj instanceof String) {
                String fieldValue = (String) fieldValueObj;
                if (fieldValue.length() > 0) {
                    if ((fieldValue.indexOf(wildcard) > -1) || (fieldValue.indexOf("%") > -1)) {
                        fieldValue = fieldValue.replaceAll("[" + wildcard + "]", "%");
                        entity.put(fieldName, fieldValue);
                        if (alias != null)
                            return alias + "." + fieldName + " like :" + fieldName;
                        return fieldName + " like :" + fieldName;
                        
                    }
                    if (alias != null)
                        return alias + "." + fieldName + " = :" + fieldName;
                    return fieldName + " = :" + fieldName;
                }
            }
        }
        return null;
    }

    public static Session createSession(Entity operator, Entity infos) throws Exception {
        Session result = new Session(operator);
        if (infos != null) {
            Map<String, Object> infoValues = infos.getValuedProperties();
            Set<String> infoNames = infoValues.keySet();
            for (String name: infoNames)
                result.setProperty(name, infoValues.get(name));
        }
        return result;
    }

    public static byte[] readFileToByte(String fileName) throws Exception {
        isFileExists(fileName);
        InputStream in = new BufferedInputStream(new FileInputStream(fileName));
        try {
            byte[] bytes = new byte[in.available()];
            in.read(bytes);
            return bytes;
        } finally {
            in.close();
        }
    }

    public static byte[] readFileToByte(File file) throws IOException {
/*        
      FileInputStream f = new FileInputStream(file);
      FileChannel fc = f.getChannel();
      try {
          MappedByteBuffer bf = fc.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
          try {
              byte[] bytes = new byte[(int) file.length()];
              bf.get(bytes);
              return bytes;
          } finally {
              bf = null;
          }
      } finally {
          fc.close();
          fc = null;
          f.close();
          f = null;
      }
*/
        FileInputStream f = new FileInputStream(file);
        FileChannel fc = f.getChannel();
        try {
            ByteBuffer bf = ByteBuffer.allocateDirect((int) file.length());
            try {
                fc.read(bf);
                bf.flip();
                byte[] bytes = new byte[(int) file.length()];
                bf.get(bytes);
                return bytes;
            } finally {
                bf.clear();
                bf = null;
            }
        } finally {
            fc.close();
            fc = null;
            f.close();
            f = null;
        }
    }

    public static byte[] readFileToByteEx(String fileName) throws Exception {
        isFileExists(fileName);
        return inputStreamToBytes(new BufferedInputStream(new FileInputStream(fileName)));
    }

    public static InputStream readFileToInputStream(String fileName) throws Exception {
        isFileExists(fileName);
        return new BufferedInputStream(new FileInputStream(fileName));
    }

    public static InputStream readFileToInputStream(File file) throws Exception {
        isFileExists(file);
        return new BufferedInputStream(new FileInputStream(file));
    }

    // read file to Base64 string
    public static String readFileToB64String(String fileName) throws Exception {
        byte[] bytes = readFileToByteEx(fileName);
        return Base64Utils.encode(bytes);
    }
    
    // Convert to InputStream from Base64 String
    public static InputStream convertB64StringToInputStream(String b64Str) throws Exception {
        byte[] docStr = Base64Utils.decode(b64Str.getBytes());
        return new BufferedInputStream(new ByteArrayInputStream(docStr));
    }
    
    public static boolean saveB64StringToFile(String b64Str, String fileName) throws Exception {
        return saveByteToFile(Base64Utils.decode(b64Str.getBytes()), fileName);
    }
    
    public static boolean saveByteToFile(byte[] data, String fileName) throws Exception {
        File f = new File(fileName);
        return saveByteToFile(data, f);
    }

    public static boolean saveByteToFile(byte[] data, File file) throws Exception {
        File parent = file.getParentFile();
        if (!parent.exists())
            parent.mkdirs();
        OutputStream fo = new BufferedOutputStream(new FileOutputStream(file));
        try {
            fo.write(data);
            return true;
        } finally {
            fo.flush();
            fo.close();
        }
    }

    public static File saveToTempFile(File root, File current, byte[] bytes, String tmpName) {
        return saveToTempFile(root, current, new ByteArrayInputStream(bytes), tmpName);
    }

    public static File saveToTempFile(File root, File current, InputStream content, String tmpName) {
        try {
            deleteFile(current);
            if (current == null) {
                File root2 = new File(root, CommonUtils.formatDateTime(new Date(), "yyyy\\DDD"));
                if (!root2.exists())
                    root2.mkdirs();
                current = File.createTempFile(tmpName, ".tmp", root2);
            }
            current.deleteOnExit();
            if (content != null) {
                InputStream in = new BufferedInputStream(content);
                OutputStream out = new BufferedOutputStream(new FileOutputStream(current));
                try {
                    IOUtils.copy(in, out);
                } catch (FileNotFoundException e) {
                    log.error(e.getLocalizedMessage(), e);
                } finally {
                    in.close();
                    out.flush();
                    out.close();
                }
            }
            return current;
        } catch(IOException e) {
            log.error(e.getLocalizedMessage(), e);
            return null;
        }
    }

    /**
     * @param owner: when owner got GC then this temp file will be deleted
     */
    private static final FileCleaningTracker tracker = new FileCleaningTracker();
    public static File saveToTempFile(Object owner, File root, File current, InputStream content, String tmpName) {
        File result = saveToTempFile(root, current, content, tmpName);
        if (owner != null)
            tracker.track(result, owner);
        return result;
    }

    public static File createTempDir(String root, String tmpDirName) {
        String tempdir = System.getProperty("java.io.tmpdir");
        File tmpDir = new File(tempdir + File.separator + root + File.separator + tmpDirName);
        if (!tmpDir.exists())
            tmpDir.mkdirs();
        return tmpDir;
    }
    
    public static void deleteFile(File file) {
        if (file != null && file.exists())
            if (!file.delete())
                log.warn("Delete fail: " + file.getAbsolutePath());
    }

    public static byte[] inputStreamToBytes(InputStream source) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        BufferedOutputStream buis = new BufferedOutputStream(bos);
        try {
            byte[] data = new byte[1024];
            int length = -1;
            while ((length = source.read(data)) != -1)
                buis.write(data, 0, length);
        } finally {
            bos.flush();
            bos.close();
            buis.flush();
            buis.close();
            source.close();
        }
        return bos.toByteArray();
    }
    
    public static String formatDateTime(String value, String valueFormat, String outputFormat) {
        return formatDateTime(value, valueFormat, outputFormat, SimpleDateFormat.class);
    }

    public static <Clazz extends SimpleDateFormat> String formatDateTime(String value,
                                                                         String valueFormat, String outputFormat,
                                                                         Class<Clazz> formatterClass) {
        try {
            SimpleDateFormat parser = formatterClass.newInstance();
            parser.applyPattern(valueFormat);
            Date dateTime = parser.parse(value);
            SimpleDateFormat format = formatterClass.newInstance();
            format.applyPattern(outputFormat);
            return format.format(dateTime);
        } catch(Exception e) {
            return null;
        }
    }

    public static String formatDateTime(Date value, String outputFormat) {
        return formatDateTime(value, outputFormat, SimpleDateFormat.class);
    }

    /**
     * @param <Clazz>
     * @param value
     * @param outputFormat
     * @param formatterClass
     * @return
     * @since 2011/06/09
     */
    public static <Clazz extends SimpleDateFormat> String formatDateTime(Date value, String outputFormat,
                                        Class<Clazz> formatterClass) {
        try {
            SimpleDateFormat format = formatterClass.newInstance();
            format.applyPattern(outputFormat);
            return format.format(value);
        } catch(Exception e) {
            return null;
        }
    }

    public static double calcAge(Date birthDate) {
        return calcAge(birthDate, new Date());
    }
    
    public static double calcAge(Date birthDate, Date eventDate) {
        // TODO, 閮��芣遛1撟�
        double age = 0;
        Calendar now = Calendar.getInstance();
        Calendar dob = Calendar.getInstance();
        now.setTime(eventDate);
        dob.setTime(birthDate);
        double year = (now.get(Calendar.YEAR) - dob.get(Calendar.YEAR));
        if ((dob.get(Calendar.MONTH) > now.get(Calendar.MONTH)) ||
            (dob.get(Calendar.MONTH) == now.get(Calendar.MONTH) &&
             dob.get(Calendar.DAY_OF_MONTH) > now.get(Calendar.DAY_OF_MONTH))) {
            year = year - 1;
/*            
            double month = ((dob.get(Calendar.MONTH) - now.get(Calendar.MONTH)) / 12);
            age = year + month;
*/            
            age = year;
        } else {
/*
            BigDecimal a = BigDecimal.valueOf((now.get(Calendar.MONTH) - dob.get(Calendar.MONTH)));
            BigDecimal b = BigDecimal.valueOf(12);
            double month = a.divide(b).doubleValue();
            age = year + month;
*/
            age = year;
        }
        return age;
    }
    
    private static void updateDateValues(DATE_FIELD_TYPE dateFieldType, 
                                         String dateFieldNameFrom, String dateFieldNameTo,
                                         Map<String, Object> entity, SimpleDateFormat formatter, 
                                         Date dateValueFrom, Date dateValueTo) throws ParseException {
        switch (dateFieldType) {
            case STRING:
                if (dateValueFrom != null)
                    entity.put(dateFieldNameFrom, formatter.format(dateValueFrom));
                if (dateValueTo != null)
                    entity.put(dateFieldNameTo, formatter.format(dateValueTo));
                break;
            case DATE:
                if (dateValueFrom != null)
                    entity.put(dateFieldNameFrom, dateValueFrom);
                if (dateValueTo != null)
                    entity.put(dateFieldNameTo, dateValueTo);
                break;
        }
    }

    public static Date parsingBirthDate(String birthDate) throws ParseException {
        return parsingDate(birthDate);
    }

    public static Date parsingDate(String value) throws ParseException {
        return parsingDateTime(value, "yyyyMMdd");
    }
    
    public static Date parsingDateTime(String value) throws ParseException {
        return parsingDateTime(value, "yyyyMMddHHmmss");
    }

    public static Date parsingDateTime(String value, String pattern) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        if ((value != null) && (value.length() == pattern.length()))
            return format.parse(value);
        throw new ParseException("value length not enough, " + pattern.length(), 0);
    }

    /*
     * return yyyyMMddHHmmss
     */
    public static String getCurrentDateTime() {
        return getCurrentDateTime("yyyyMMddHHmmss");
    }
    
    /**
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String getCurrentDateTime2() {
        return getCurrentDateTime("yyyy-MM-dd HH:mm:ss");
    }
    
    /**
     * @return yyyyMMdd
     */
    public static String getCurrentDate() {
        return getCurrentDateTime("yyyyMMdd");
    }
    
    /**
     * @return yyyy-MM-dd
     */
    public static String getCurrentDate2() {
        return getCurrentDateTime("yyyy-MM-dd");
    }
    
    /**
     * @return HHmmss
     */
    public static String getCurrentTime() {
        return getCurrentDateTime("HHmmss");
    }
    
    /**
     * @return HH:mm:ss
     */
    public static String getCurrentTime2() {
        return getCurrentDateTime("HH:mm:ss");
    }
    
    public static String getCurrentDateTime(String format) {
        return getDateTime(Calendar.getInstance().getTime(), format);
    }
    
    public static String getCurrentGMTDateTime() {
        Calendar c = Calendar.getInstance();
        return getGMTDateTime(c.getTime());
    }
    
    /**
     * @return yyyyMMddHHmmss
     */
    public static String getDateTime(Date value) {
        return getDateTime(value, "yyyyMMddHHmmss");
    }
    
    /**
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String getDateTime2(Date value) {
        return getDateTime(value, "yyyy-MM-dd HH:mm:ss");
    }
    
    /**
     * @return yyyyMMdd
     */
    public static String getDate(Date value) {
        return getDateTime(value, "yyyyMMdd");
    }
    
    /**
     * @return yyyy-MM-dd
     */
    public static String getDate2(Date value) {
        return getDateTime(value, "yyyy-MM-dd");
    }
    
    public static String getDateTime(Date value, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        if (value != null)
            return format.format(value);
        return null;
    }
    
    public static String getGMTDateTime(Date value) {
        Calendar c = Calendar.getInstance();
        int tzOffset = c.getTimeZone().getRawOffset() / 1000 / 60 / 60;
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss+" + ((tzOffset < 10) ? "0" + tzOffset + "00" : tzOffset + "00"));
        return format.format(value);
    }
    
    public static boolean checkFileExists(String fileName) {
        try {
            return isFileExists(fileName);
        } catch (FileNotFoundException e) {
        }
        return false;
    }
    
    public static boolean isFileExists(String fileName) throws FileNotFoundException {
        return isFileExists(new File(fileName));
    }

    public static boolean isFileExists(File file) throws FileNotFoundException {
        try {
            if (!file.exists()) {
                URI uri = file.toURI();
                String msg = "File not found: " + uri.toString();
                throw new FileNotFoundException(msg);
            }
        } catch (NullPointerException e) {
        }
        return true;
    }

    /**
     * @param b
     * @return
     * @since 2011/06/01
     */
    public static String byteToHex(byte b){
        return ("" + "0123456789ABCDEF".charAt(0xf&b >> 4) + "0123456789ABCDEF".charAt(b&0xf));
    }

    public static String wrap(String txt, int maxLength) {
        return WordUtils.wrap(txt, maxLength);
    }
        
    public static String rTrim(String source) {
        return source.replaceAll("\\s+$", "");
    }

    public static byte[] objectToBytes(Object object) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedOutputStream bos = new BufferedOutputStream(baos);
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        try {
            oos.writeObject(object);
        } finally {
            oos.flush();
            oos.close();
            bos.flush();
            bos.close();
            baos.flush();
            baos.close();
        }
        return baos.toByteArray();
    }

    public static <T> byte[] objectsToBytes(List<T> objectList) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedOutputStream bos = new BufferedOutputStream(baos);
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        try {
            for (Object obj: objectList)
                oos.writeObject(obj);
        } finally {
            oos.flush();
            oos.close();
            bos.flush();
            bos.close();
            baos.flush();
            baos.close();
        }
        return baos.toByteArray();
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T bytesToObject(byte[] source) throws IOException, ClassNotFoundException {
        T obj = null;
        ByteArrayInputStream bais = new ByteArrayInputStream(source);
        BufferedInputStream bis = new BufferedInputStream(bais);
        ObjectInputStream ois = new ObjectInputStream(bis);
        try {
            obj = (T) ois.readObject();
        } catch (EOFException e) { //This exception will be caught when EOF is reached
        } finally {
            ois.close();
            bis.close();
            bais.close();
        }
        return obj;
    }
    
    @SuppressWarnings("unchecked")
    public static <T> List<T> bytesToObjects(byte[] source) throws IOException, ClassNotFoundException {
        List<T> result = new ArrayList<T>();
        
        ByteArrayInputStream bais = new ByteArrayInputStream(source);
        BufferedInputStream bis = new BufferedInputStream(bais);
        ObjectInputStream ois = new ObjectInputStream(bis);
        try {
            T obj = null;
            while ((obj = (T) ois.readObject()) != null) {
                result.add(obj);
            }
        } catch (EOFException e) { //This exception will be caught when EOF is reached
        } finally {
            ois.close();
            bis.close();
            bais.close();
        }
        return result;
    }

}
