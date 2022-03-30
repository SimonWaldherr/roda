package org.roda.core.data.utils;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.roda.core.data.common.RodaConstants;
import org.roda.core.data.v2.synchronization.bundle.LastSynchronizationState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;

/**
 * {@author João Gomes <jgomes@keep.pt>}.
 */
public class LastSynchronizationReportUtils {
  private static final Logger LOGGER = LoggerFactory.getLogger(LastSynchronizationReportUtils.class);
  private static OutputStream outputStream = null;
  private static JsonGenerator jsonGenerator = null;

  public LastSynchronizationReportUtils() {
    // do Nothing
  }

  public static void init(Path path) throws IOException {
    if (!Files.exists(path)) {
      Files.createFile(path);
    }

    if (path != null) {
      outputStream = new BufferedOutputStream(new FileOutputStream(path.toFile()));
      final JsonFactory jsonFactory = new JsonFactory();
      jsonGenerator = jsonFactory.createGenerator(outputStream, JsonEncoding.UTF8).useDefaultPrettyPrinter();
    }

    jsonGenerator.writeStartArray();
  }

  public static void writeString(String value) throws IOException {
    jsonGenerator.writeString(value);
  }

  /**
   * Write the {@link LastSynchronizationState} lists to a file.
   * 
   * @param lastSynchronizationState
   *          {@link LastSynchronizationState}.
   * @param path
   *          {@link Path}.
   * @throws IOException
   *           if some i/o error occurs.
   */
  public static void writeJsonToFile(final LastSynchronizationState lastSynchronizationState, final Path path)
    throws IOException {
    if (!Files.exists(path)) {
      Files.createFile(path);
    }
    try {
      if (path != null) {
        outputStream = new BufferedOutputStream(new FileOutputStream(path.toFile()));
        final JsonFactory jsonFactory = new JsonFactory();
        jsonGenerator = jsonFactory.createGenerator(outputStream, JsonEncoding.UTF8).useDefaultPrettyPrinter();
      }

      jsonGenerator.writeStartObject();
      jsonGenerator.writeStringField(RodaConstants.SYNCHRONIZATION_REPORT_KEY_UUID, lastSynchronizationState.getUuid());
      jsonGenerator.writeStringField(RodaConstants.SYNCHRONIZATION_REPORT_KEY_INSTANCE_ID,
        lastSynchronizationState.getInstance_id());
      jsonGenerator.writeStringField(RodaConstants.SYNCHRONIZATION_REPORT_KEY_TO_DATE,
        lastSynchronizationState.getTo_date());
      jsonGenerator.writeStringField(RodaConstants.SYNCHRONIZATION_REPORT_KEY_FROM_DATE,
        lastSynchronizationState.getFrom_date());
      jsonGenerator.writeStringField(RodaConstants.SYNCHRONIZATION_REPORT_KEY_STATUS, null);
      jsonGenerator.writeStringField(RodaConstants.SYNCHRONIZATION_REPORT_KEY_JOB, null);
      jsonGenerator.writeFieldName(RodaConstants.SYNCHRONIZATION_REPORT_KEY_ADDED);
      writeObjectWithDescriptionAndCount(jsonGenerator, RodaConstants.SYNCHRONIZATION_REPORT_DESCRIPTION_CENTRAL_ADDED,
        0);
      jsonGenerator.writeFieldName(RodaConstants.SYNCHRONIZATION_REPORT_KEY_UPDATED);
      writeObjectWithDescriptionAndCount(jsonGenerator, RodaConstants.SYNCHRONIZATION_REPORT_DESCRIPTION_CENTRAL_ADDED,
        0);

      // Removed Entities
      jsonGenerator.writeFieldName(RodaConstants.SYNCHRONIZATION_REPORT_KEY_REMOVED);
      jsonGenerator.writeStartObject();
      jsonGenerator.writeStringField(RodaConstants.SYNCHRONIZATION_REPORT_KEY_DESCRIPTION,
        RodaConstants.SYNCHRONIZATION_REPORT_DESCRIPTION_CENTRAL_REMOVED);
      jsonGenerator.writeNumberField(RodaConstants.SYNCHRONIZATION_REPORT_KEY_COUNT,
        lastSynchronizationState.countRemovedEntities());
      writeEntitiesObject(jsonGenerator, lastSynchronizationState.getRemovedEntities());
      jsonGenerator.writeEndObject();

      // Issues
      jsonGenerator.writeFieldName(RodaConstants.SYNCHRONIZATION_REPORT_KEY_ISSUES);
      jsonGenerator.writeStartArray();
      jsonGenerator.writeStartObject();
      jsonGenerator.writeStringField(RodaConstants.SYNCHRONIZATION_REPORT_KEY_ISSUE_TYPE,
        RodaConstants.SYNCHRONIZATION_REPORT_ISSUE_TYPE_MISSING);
      jsonGenerator.writeStringField(RodaConstants.SYNCHRONIZATION_REPORT_KEY_DESCRIPTION,
        RodaConstants.SYNCHRONIZATION_REPORT_ISSUE_DESCRIPTION_MISSING);
      writeEntitiesObject(jsonGenerator, lastSynchronizationState.getIssues());
      jsonGenerator.writeEndObject();
      jsonGenerator.writeEndArray();

      jsonGenerator.writeEndObject();
    } catch (final IOException e) {
      LOGGER.error("Can't create report for removed entities {}", e.getMessage());
    } finally {
      close(false);
    }
  }

  /**
   * Write an JSON array in the file.
   * 
   * @param jsonGenerator
   *          {@link JsonGenerator}.
   * @param array
   *          {@link List}.
   * @param key
   *          the key to array object.
   * @throws IOException
   *           if some i/o error occurs.
   */
  private static void writeJsonArray(final JsonGenerator jsonGenerator, List<String> array, String key)
    throws IOException {
    jsonGenerator.writeFieldName(key);
    jsonGenerator.writeStartArray();
    if (!array.isEmpty()) {
      for (String value : array) {
        jsonGenerator.writeString(value);
      }
    }
    jsonGenerator.writeEndArray();
  }

  /**
   * Close the {@link JsonGenerator} and the {@link OutputStream}.
   *
   * @throws IOException
   *           if some i/o error occurs.
   */
  public static void close(boolean closeArray) throws IOException {
    if (closeArray) {
      jsonGenerator.writeEndArray();
    }

    if (jsonGenerator != null) {
      jsonGenerator.close();
    }
    if (outputStream != null) {
      outputStream.close();
    }

  }

  /**
   * Create {@link JsonParser} to read the file.
   * 
   * @param path
   *          {@link Path}.
   * @return {@link JsonParser}.
   * @throws IOException
   *           if some i/o error occurs.
   */
  public static JsonParser createJsonParser(Path path) throws IOException {
    final JsonFactory jfactory = new JsonFactory();
    return jfactory.createParser(path.toFile());
  }

  private static void writeObjectWithDescriptionAndCount(final JsonGenerator jsonGenerator, final String description,
    final int count) throws IOException {
    jsonGenerator.writeStartObject();
    jsonGenerator.writeStringField(RodaConstants.SYNCHRONIZATION_REPORT_KEY_DESCRIPTION, description);
    jsonGenerator.writeNumberField(RodaConstants.SYNCHRONIZATION_REPORT_KEY_COUNT, count);
    jsonGenerator.writeEndObject();
  }

  private static void writeEntitiesObject(final JsonGenerator jsonGenerator, final Map<String, List<String>> entities)
    throws IOException {
    jsonGenerator.writeFieldName(RodaConstants.SYNCHRONIZATION_REPORT_KEY_ENTITIES);
    jsonGenerator.writeStartArray();
    for (Map.Entry<String, List<String>> entity : entities.entrySet()) {
      jsonGenerator.writeStartObject();
      jsonGenerator.writeStringField(RodaConstants.SYNCHRONIZATION_REPORT_KEY_ENTITY_CLASS, entity.getKey());
      jsonGenerator.writeFieldName(RodaConstants.SYNCHRONIZATION_REPORT_KEY_ENTITY_IDS);
      jsonGenerator.writeStartArray();
      for (String entityId : entity.getValue()) {
        jsonGenerator.writeString(entityId);
      }
      jsonGenerator.writeEndArray();
      jsonGenerator.writeEndObject();
    }
    jsonGenerator.writeEndArray();
  }
}
