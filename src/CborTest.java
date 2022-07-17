
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.zoolu.util.Bytes;
import org.zoolu.util.json.JsonMember;
import org.zoolu.util.json.JsonNumber;
import org.zoolu.util.json.JsonObject;

import jacob.CborEncoder;

public final class CborTest {
	private CborTest() {}

	public static void main(String[] args) throws IOException {	
		// CBOR
		String NAME="temperature";
		double VALUE=12.3;
		ByteArrayOutputStream baos= new ByteArrayOutputStream();
		CborEncoder enc= new CborEncoder(baos);
		enc.writeMapStart(1);
		//enc.writeString(3,NAME.getBytes());
		enc.writeTextString(NAME);
		//enc.writeFloat((float)VALUE);
		enc.writeDouble(VALUE);
		//enc.writeInt(23);
		System.out.println(Bytes.toHex(baos.toByteArray()));
		// JSON
		System.out.println("{\""+NAME+"\":"+VALUE+"}");
		System.out.println(new JsonObject(new JsonMember(NAME,new JsonNumber(VALUE))));
}

}
