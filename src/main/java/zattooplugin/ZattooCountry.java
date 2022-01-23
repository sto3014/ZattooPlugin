package zattooplugin;

public class ZattooCountry {
   private String mCode;
   private String mName;


   public ZattooCountry(String code, String name) {
      this.mCode = code;
      this.mName = name;
   }

   public String getCode() {
      return this.mCode;
   }

   public void setCode(String code) {
      this.mCode = code;
   }

   public String getName() {
      return this.mName;
   }

   public void setName(String name) {
      this.mName = name;
   }

   public String toString() {
      return this.mName;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         ZattooCountry that = (ZattooCountry)o;
         if (this.mCode != null) {
            if (!this.mCode.equals(that.mCode)) {
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
      return this.mCode != null ? this.mCode.hashCode() : 0;
   }
}
