package zattooplugin;

/**
 * The type Zattoo settings.
 *
 * @author Bodo Tasche, Michael Keppler
 * @since 1.0.0.0
 */
public class ZattooCountry {
   private String mCode;
   private String mName;


   /**
    * Instantiates a new Zattoo country.
    *
    * @param code the code
    * @param name the name
    */
   public ZattooCountry(String code, String name) {
      mCode = code;
      mName = name;
   }

   /**
    * Gets code.
    *
    * @return the code
    */
   public String getCode() {
      return mCode;
   }

   /**
    * Sets code.
    *
    * @param code the code
    */
   public void setCode(String code) {
      mCode = code;
   }

   /**
    * Gets name.
    *
    * @return the name
    */
   public String getName() {
      return mName;
   }

   /**
    * Sets name.
    *
    * @param name the name
    */
   public void setName(String name) {
      mName = name;
   }

   public String toString() {
      return mName;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && getClass() == o.getClass()) {
         ZattooCountry that = (ZattooCountry)o;
         if (mCode != null) {
            if (!mCode.equals(that.mCode)) {
               return false;
            }
         } else if (that.mCode != null) {
            return false;
         }

         return true;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return mCode != null ? mCode.hashCode() : 0;
   }
}
