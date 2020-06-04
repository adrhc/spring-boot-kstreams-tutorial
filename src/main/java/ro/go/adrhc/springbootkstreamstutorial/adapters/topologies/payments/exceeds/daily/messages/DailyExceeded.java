/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package ro.go.adrhc.springbootkstreamstutorial.adapters.topologies.payments.exceeds.daily.messages;

import org.apache.avro.generic.GenericArray;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.util.Utf8;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.SchemaStore;

@org.apache.avro.specific.AvroGenerated
public class DailyExceeded extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = -8467614137652704827L;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"DailyExceeded\",\"namespace\":\"ro.go.adrhc.springbootkstreamstutorial.adapters.topologies.payments.exceeds.daily.messages\",\"fields\":[{\"name\":\"dailyMaxAmount\",\"type\":\"int\"},{\"name\":\"dailyTotalSpent\",\"type\":{\"type\":\"record\",\"name\":\"DailyTotalSpent\",\"fields\":[{\"name\":\"clientId\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"time\",\"type\":{\"type\":\"int\",\"logicalType\":\"date\"}},{\"name\":\"amount\",\"type\":\"int\"}]}}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static SpecificData MODEL$ = new SpecificData();
static {
    MODEL$.addLogicalTypeConversion(new org.apache.avro.data.TimeConversions.DateConversion());
  }

  private static final BinaryMessageEncoder<DailyExceeded> ENCODER =
      new BinaryMessageEncoder<DailyExceeded>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<DailyExceeded> DECODER =
      new BinaryMessageDecoder<DailyExceeded>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageEncoder instance used by this class.
   * @return the message encoder used by this class
   */
  public static BinaryMessageEncoder<DailyExceeded> getEncoder() {
    return ENCODER;
  }

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   * @return the message decoder used by this class
   */
  public static BinaryMessageDecoder<DailyExceeded> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
   */
  public static BinaryMessageDecoder<DailyExceeded> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<DailyExceeded>(MODEL$, SCHEMA$, resolver);
  }

  /**
   * Serializes this DailyExceeded to a ByteBuffer.
   * @return a buffer holding the serialized data for this instance
   * @throws java.io.IOException if this instance could not be serialized
   */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /**
   * Deserializes a DailyExceeded from a ByteBuffer.
   * @param b a byte buffer holding serialized data for an instance of this class
   * @return a DailyExceeded instance decoded from the given buffer
   * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
   */
  public static DailyExceeded fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

   private int dailyMaxAmount;
   private ro.go.adrhc.springbootkstreamstutorial.adapters.topologies.payments.exceeds.daily.messages.DailyTotalSpent dailyTotalSpent;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public DailyExceeded() {}

  /**
   * All-args constructor.
   * @param dailyMaxAmount The new value for dailyMaxAmount
   * @param dailyTotalSpent The new value for dailyTotalSpent
   */
  public DailyExceeded(java.lang.Integer dailyMaxAmount, ro.go.adrhc.springbootkstreamstutorial.adapters.topologies.payments.exceeds.daily.messages.DailyTotalSpent dailyTotalSpent) {
    this.dailyMaxAmount = dailyMaxAmount;
    this.dailyTotalSpent = dailyTotalSpent;
  }

  public org.apache.avro.specific.SpecificData getSpecificData() { return MODEL$; }
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return dailyMaxAmount;
    case 1: return dailyTotalSpent;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: dailyMaxAmount = (java.lang.Integer)value$; break;
    case 1: dailyTotalSpent = (ro.go.adrhc.springbootkstreamstutorial.adapters.topologies.payments.exceeds.daily.messages.DailyTotalSpent)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'dailyMaxAmount' field.
   * @return The value of the 'dailyMaxAmount' field.
   */
  public int getDailyMaxAmount() {
    return dailyMaxAmount;
  }


  /**
   * Sets the value of the 'dailyMaxAmount' field.
   * @param value the value to set.
   */
  public void setDailyMaxAmount(int value) {
    this.dailyMaxAmount = value;
  }

  /**
   * Gets the value of the 'dailyTotalSpent' field.
   * @return The value of the 'dailyTotalSpent' field.
   */
  public ro.go.adrhc.springbootkstreamstutorial.adapters.topologies.payments.exceeds.daily.messages.DailyTotalSpent getDailyTotalSpent() {
    return dailyTotalSpent;
  }


  /**
   * Sets the value of the 'dailyTotalSpent' field.
   * @param value the value to set.
   */
  public void setDailyTotalSpent(ro.go.adrhc.springbootkstreamstutorial.adapters.topologies.payments.exceeds.daily.messages.DailyTotalSpent value) {
    this.dailyTotalSpent = value;
  }

  /**
   * Creates a new DailyExceeded RecordBuilder.
   * @return A new DailyExceeded RecordBuilder
   */
  public static ro.go.adrhc.springbootkstreamstutorial.adapters.topologies.payments.exceeds.daily.messages.DailyExceeded.Builder newBuilder() {
    return new ro.go.adrhc.springbootkstreamstutorial.adapters.topologies.payments.exceeds.daily.messages.DailyExceeded.Builder();
  }

  /**
   * Creates a new DailyExceeded RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new DailyExceeded RecordBuilder
   */
  public static ro.go.adrhc.springbootkstreamstutorial.adapters.topologies.payments.exceeds.daily.messages.DailyExceeded.Builder newBuilder(ro.go.adrhc.springbootkstreamstutorial.adapters.topologies.payments.exceeds.daily.messages.DailyExceeded.Builder other) {
    if (other == null) {
      return new ro.go.adrhc.springbootkstreamstutorial.adapters.topologies.payments.exceeds.daily.messages.DailyExceeded.Builder();
    } else {
      return new ro.go.adrhc.springbootkstreamstutorial.adapters.topologies.payments.exceeds.daily.messages.DailyExceeded.Builder(other);
    }
  }

  /**
   * Creates a new DailyExceeded RecordBuilder by copying an existing DailyExceeded instance.
   * @param other The existing instance to copy.
   * @return A new DailyExceeded RecordBuilder
   */
  public static ro.go.adrhc.springbootkstreamstutorial.adapters.topologies.payments.exceeds.daily.messages.DailyExceeded.Builder newBuilder(ro.go.adrhc.springbootkstreamstutorial.adapters.topologies.payments.exceeds.daily.messages.DailyExceeded other) {
    if (other == null) {
      return new ro.go.adrhc.springbootkstreamstutorial.adapters.topologies.payments.exceeds.daily.messages.DailyExceeded.Builder();
    } else {
      return new ro.go.adrhc.springbootkstreamstutorial.adapters.topologies.payments.exceeds.daily.messages.DailyExceeded.Builder(other);
    }
  }

  /**
   * RecordBuilder for DailyExceeded instances.
   */
  @org.apache.avro.specific.AvroGenerated
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<DailyExceeded>
    implements org.apache.avro.data.RecordBuilder<DailyExceeded> {

    private int dailyMaxAmount;
    private ro.go.adrhc.springbootkstreamstutorial.adapters.topologies.payments.exceeds.daily.messages.DailyTotalSpent dailyTotalSpent;
    private ro.go.adrhc.springbootkstreamstutorial.adapters.topologies.payments.exceeds.daily.messages.DailyTotalSpent.Builder dailyTotalSpentBuilder;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(ro.go.adrhc.springbootkstreamstutorial.adapters.topologies.payments.exceeds.daily.messages.DailyExceeded.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.dailyMaxAmount)) {
        this.dailyMaxAmount = data().deepCopy(fields()[0].schema(), other.dailyMaxAmount);
        fieldSetFlags()[0] = other.fieldSetFlags()[0];
      }
      if (isValidValue(fields()[1], other.dailyTotalSpent)) {
        this.dailyTotalSpent = data().deepCopy(fields()[1].schema(), other.dailyTotalSpent);
        fieldSetFlags()[1] = other.fieldSetFlags()[1];
      }
      if (other.hasDailyTotalSpentBuilder()) {
        this.dailyTotalSpentBuilder = ro.go.adrhc.springbootkstreamstutorial.adapters.topologies.payments.exceeds.daily.messages.DailyTotalSpent.newBuilder(other.getDailyTotalSpentBuilder());
      }
    }

    /**
     * Creates a Builder by copying an existing DailyExceeded instance
     * @param other The existing instance to copy.
     */
    private Builder(ro.go.adrhc.springbootkstreamstutorial.adapters.topologies.payments.exceeds.daily.messages.DailyExceeded other) {
      super(SCHEMA$);
      if (isValidValue(fields()[0], other.dailyMaxAmount)) {
        this.dailyMaxAmount = data().deepCopy(fields()[0].schema(), other.dailyMaxAmount);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.dailyTotalSpent)) {
        this.dailyTotalSpent = data().deepCopy(fields()[1].schema(), other.dailyTotalSpent);
        fieldSetFlags()[1] = true;
      }
      this.dailyTotalSpentBuilder = null;
    }

    /**
      * Gets the value of the 'dailyMaxAmount' field.
      * @return The value.
      */
    public int getDailyMaxAmount() {
      return dailyMaxAmount;
    }


    /**
      * Sets the value of the 'dailyMaxAmount' field.
      * @param value The value of 'dailyMaxAmount'.
      * @return This builder.
      */
    public ro.go.adrhc.springbootkstreamstutorial.adapters.topologies.payments.exceeds.daily.messages.DailyExceeded.Builder setDailyMaxAmount(int value) {
      validate(fields()[0], value);
      this.dailyMaxAmount = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'dailyMaxAmount' field has been set.
      * @return True if the 'dailyMaxAmount' field has been set, false otherwise.
      */
    public boolean hasDailyMaxAmount() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'dailyMaxAmount' field.
      * @return This builder.
      */
    public ro.go.adrhc.springbootkstreamstutorial.adapters.topologies.payments.exceeds.daily.messages.DailyExceeded.Builder clearDailyMaxAmount() {
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'dailyTotalSpent' field.
      * @return The value.
      */
    public ro.go.adrhc.springbootkstreamstutorial.adapters.topologies.payments.exceeds.daily.messages.DailyTotalSpent getDailyTotalSpent() {
      return dailyTotalSpent;
    }


    /**
      * Sets the value of the 'dailyTotalSpent' field.
      * @param value The value of 'dailyTotalSpent'.
      * @return This builder.
      */
    public ro.go.adrhc.springbootkstreamstutorial.adapters.topologies.payments.exceeds.daily.messages.DailyExceeded.Builder setDailyTotalSpent(ro.go.adrhc.springbootkstreamstutorial.adapters.topologies.payments.exceeds.daily.messages.DailyTotalSpent value) {
      validate(fields()[1], value);
      this.dailyTotalSpentBuilder = null;
      this.dailyTotalSpent = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'dailyTotalSpent' field has been set.
      * @return True if the 'dailyTotalSpent' field has been set, false otherwise.
      */
    public boolean hasDailyTotalSpent() {
      return fieldSetFlags()[1];
    }

    /**
     * Gets the Builder instance for the 'dailyTotalSpent' field and creates one if it doesn't exist yet.
     * @return This builder.
     */
    public ro.go.adrhc.springbootkstreamstutorial.adapters.topologies.payments.exceeds.daily.messages.DailyTotalSpent.Builder getDailyTotalSpentBuilder() {
      if (dailyTotalSpentBuilder == null) {
        if (hasDailyTotalSpent()) {
          setDailyTotalSpentBuilder(ro.go.adrhc.springbootkstreamstutorial.adapters.topologies.payments.exceeds.daily.messages.DailyTotalSpent.newBuilder(dailyTotalSpent));
        } else {
          setDailyTotalSpentBuilder(ro.go.adrhc.springbootkstreamstutorial.adapters.topologies.payments.exceeds.daily.messages.DailyTotalSpent.newBuilder());
        }
      }
      return dailyTotalSpentBuilder;
    }

    /**
     * Sets the Builder instance for the 'dailyTotalSpent' field
     * @param value The builder instance that must be set.
     * @return This builder.
     */
    public ro.go.adrhc.springbootkstreamstutorial.adapters.topologies.payments.exceeds.daily.messages.DailyExceeded.Builder setDailyTotalSpentBuilder(ro.go.adrhc.springbootkstreamstutorial.adapters.topologies.payments.exceeds.daily.messages.DailyTotalSpent.Builder value) {
      clearDailyTotalSpent();
      dailyTotalSpentBuilder = value;
      return this;
    }

    /**
     * Checks whether the 'dailyTotalSpent' field has an active Builder instance
     * @return True if the 'dailyTotalSpent' field has an active Builder instance
     */
    public boolean hasDailyTotalSpentBuilder() {
      return dailyTotalSpentBuilder != null;
    }

    /**
      * Clears the value of the 'dailyTotalSpent' field.
      * @return This builder.
      */
    public ro.go.adrhc.springbootkstreamstutorial.adapters.topologies.payments.exceeds.daily.messages.DailyExceeded.Builder clearDailyTotalSpent() {
      dailyTotalSpent = null;
      dailyTotalSpentBuilder = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public DailyExceeded build() {
      try {
        DailyExceeded record = new DailyExceeded();
        record.dailyMaxAmount = fieldSetFlags()[0] ? this.dailyMaxAmount : (java.lang.Integer) defaultValue(fields()[0]);
        if (dailyTotalSpentBuilder != null) {
          try {
            record.dailyTotalSpent = this.dailyTotalSpentBuilder.build();
          } catch (org.apache.avro.AvroMissingFieldException e) {
            e.addParentField(record.getSchema().getField("dailyTotalSpent"));
            throw e;
          }
        } else {
          record.dailyTotalSpent = fieldSetFlags()[1] ? this.dailyTotalSpent : (ro.go.adrhc.springbootkstreamstutorial.adapters.topologies.payments.exceeds.daily.messages.DailyTotalSpent) defaultValue(fields()[1]);
        }
        return record;
      } catch (org.apache.avro.AvroMissingFieldException e) {
        throw e;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<DailyExceeded>
    WRITER$ = (org.apache.avro.io.DatumWriter<DailyExceeded>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<DailyExceeded>
    READER$ = (org.apache.avro.io.DatumReader<DailyExceeded>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

}










