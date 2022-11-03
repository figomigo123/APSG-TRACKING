package org.traccar.protocol;

import org.junit.Test;
import org.traccar.ProtocolTest;

public class NavisProtocolDecoderTest extends ProtocolTest {

    @Test
    public void testDecodeNtcb() throws Exception {

        var decoder = new NavisProtocolDecoder(null);

        verifyNull(decoder, binary(
                "404E5443010000007B000000130044342A3E533A383631373835303035323035303739"));

        verifyNull(decoder, binary(
                "404E5443010000007B000000130047372A3E533A383631373835303035313236303639"));

        verifyPosition(decoder, binary(
                "404e5443010000000000000059009adb2a3e54250000000000ff1500040b0a1008291838001200760ee600000000000000000000000f1500040b0a10ac20703fb1aec23f00000000320149668f430000000000000000000000000000000000000000000000f3808080"),
                position("2016-11-11 21:00:04.000", true, 53.74336, 87.14437));

        verifyPositions(decoder, binary(
                "404e544300000000040000005a00c6812a3e410125e3a60700011705071503011030210c0000fa200910e6000000000000000000000001082106150010ae97643f88a39f3f0000000090001fcc6c450000000000000000000000000000000000000000000000f6808080"));

        verifyPositions(decoder, binary(
                "404e544301000000000000005a002e6c2a3e410125d7540100001512233a0b0a0f08026300000a000b000b00020000000000000000000c12233b0b0a0f03fd6d3f0fde603f00000000ba0051e0c845000000000000000000000000000000000000000000000080808080"));

        verifyPositions(decoder, binary(
                "404E5443010000007B0000005A0050692A3E410125DB0E00000015110707110A0C0880630000AA39A2381600020000000000000000000C110708110A0CB389793F1AEF263F00000000120034F516440000000000000000000000FAFF000000FAFF000000FAFF80808080"));

        verifyPosition(decoder, binary(
                "404e544301000000cdfbf5027200852e2a3e5406aa170000c11116162410001310a9110e80996b281003000a0008000000000000000000d207d207ffffff00fbff00fbff00fbff00fbff00fbff00fbff00fbff2d808080ffffffffffff2b161624100013509b0302b0f89201830500000000000037002fb8cf43eed5843a35003500"),
                position("2019-01-16 22:22:36.000", true, 56.31800, 44.01523));

        verifyPositions(decoder, binary(
                "404e54430100000045635902730081972a3e4101060b7e0e000b171328050d00133029110e00bc6141100200000000000000000000000000d207d307ffffff00fbff00fbff00fbff00fbff00fbff00fbff00fbff02808080ffffffffffff4f1328050d001371cd0302c5109101a60300000000000000003d1b37470000000096009600"));
    }

    @Test
    public void testDecodeFlex10() throws Exception {

        var decoder = new NavisProtocolDecoder(null);

        verifyNull(decoder, binary(
                "404e544301000000c9b5f602130046c52a3e533a383639363936303439373232383235"));

        verifyNull(decoder, binary(
                "404e544301000000aaecf6021300c8712a3e464c4558b00a0a45ffff300a08080f8388"));

        verifyPosition(decoder, binary(
                "7e54040000000400000030129957405c000b00632f9857405ccace03021e129101a103000000000000c4005ba3fe3b00000000120046100000000000001aff7f000080bfffff80000080bfffffffff9f"),
                position("2019-01-17 10:23:20.000", true, 56.33996, 43.80762));

        verifyPositions(decoder, binary(
                "7e4101080000000917c057405c002b001833c057405cbbce030225129101a00300007c6102408900400c1b3cfce3b23a12004710e000000000001bff7f000080bfffff80000080bfffffffffb2"));
    }

    @Test
    public void testDecodeFlex20() throws Exception {

        var decoder = new NavisProtocolDecoder(null);

        verifyNull(decoder, binary(
                "404e544301000000a9eef602130043fb2a3e533a383639363936303439373337333835"));

        verifyNull(decoder, binary(
                "404e544301000000a9eef6021a003f8e2a3e464c4558b014147afffff008080800000e00000000000000"));

        verifyPosition(decoder, binary(
                "7e5428000000280000002111d16b435c00a900154bd16b435ce19e030259f6920133050000b7623e429300c9e7f03f2ba45a3e1f001f007b6c5910850f0100001629080a000000000000060947"),
                position("2019-01-19 18:26:25.000", true, 56.31952, 44.01423));

        verifyPositions(decoder, binary(
                "7e4101270000000b17b16b435c00a9000d4bb26b435caaa2030229f29201620500000000000093004493d53fee892d3e1f001f00ac6c591081f00000001700080a0000000000000609f2"));
    }

}
