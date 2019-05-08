package de.danielprinz.hskl.nk.rsa.crypto;

import de.danielprinz.hskl.nk.rsa.Main;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Arrays;
import java.util.Map;

public class KryproManager {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    //private static final Charset CHARSET = Charset.forName("windows-1251");
    private static final Charset CHARSET = StandardCharsets.UTF_16BE;

    static {
        SECURE_RANDOM.setSeed(4299209391323882146L);
    }


    public static byte[] encrypt(Key publicKey, String msg) throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/NoPadding");
        rsaCipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return rsaCipher.doFinal(msg.getBytes(StandardCharsets.UTF_8));
    }

    public static String decrypt(PrivateKey privateKey, byte[] msg) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/NoPadding");
        rsaCipher.init(Cipher.DECRYPT_MODE , privateKey);
        byte[] decrypted = rsaCipher.doFinal(msg);
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    /**
     * Generates a fresh, random pair of private and public RSA key
     * @param keysize The size of the key which should be generated
     * @return The keypair
     * @throws NoSuchAlgorithmException
     */
    public static KeyPair getFreshKeyPair(int keysize) throws NoSuchAlgorithmException {
        if(keysize < 512) throw new IllegalArgumentException("Keysize must be greater than 512");
        if(keysize > 16384) throw new IllegalArgumentException("Keysize must be less than 16385");

        Main.LOGGER.info("Generating " + keysize + " bit long keypair, this could take a while...");
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(keysize, SECURE_RANDOM);
        Main.LOGGER.info("Successfully generated the keypair.");
        return keyGen.generateKeyPair();
    }


    /**
     * Lists all possible charsets which can be used to encode or decode the string returned by {@link #encrypt(Key, String)} or used by {@link #decrypt(PrivateKey, byte[])}
     * @param keyPair The key pair that should be used for the calculation. null for a random key pair.
     * @throws IllegalBlockSizeException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     */
    public static void listPossibleCharsets(KeyPair keyPair) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        if(keyPair == null) keyPair = getFreshKeyPair(1024);
        byte[] encrypted = encrypt(keyPair.getPublic(), "Top Secret message");
        for(Map.Entry<String, Charset> entry : Charset.availableCharsets().entrySet()) {
            try {
                String s = new String(encrypted, entry.getValue());
                byte[] b = s.getBytes(entry.getValue());
                if (Arrays.equals(encrypted, b)) {
                    Main.LOGGER.info(entry.getKey());
                    Main.LOGGER.info(new String(encrypted, entry.getValue()));
                    Main.LOGGER.info("");
                }
            } catch (UnsupportedOperationException ignored) {

            }
        }
    }

    // supported charsets:
    // IBM00858,
    // IBM037, IBM1026, IBM1047, IBM273, IBM277, IBM278, IBM280, IBM284, IBM285, IBM297, IBM437, IBM500, IBM775,
    // IBM850, IBM852, IBM855, IBM860, IBM861, IBM862, IBM863, IBM865, IBM866, IBM868, IBM870, IBM871, IBM918,
    // ISO-8859-1, ISO-8859-13, ISO-8859-15, ISO-8859-2, ISO-8859-4, ISO-8859-5, ISO-8859-9,
    // KOI8-R, KOI8-U,
    // UTF-16BE,
    // windows-1251, windows-1256,
    // x-IBM1006, x-IBM1025, x-IBM1046, x-IBM1097, x-IBM1112, x-IBM1122, x-IBM1123, x-IBM1124, x-IBM1166, x-IBM737, x-IBM921, x-IBM922,
    // x-MacCentralEurope, x-MacCroatian, x-MacCyrillic, x-MacGreek, x-MacIceland, x-MacRoman, x-MacRomania, x-MacUkraine


            /*
                IBM00858, 	Z4­{Kü]üÉr<(€¬âäÅ═t6üü?'Su╚j╠Iýº¨§
                _▄#wÂÚN|É▓█­ã»@_\A«{þ1àÇøUõäW╩ý├v"îÜ═▄oý³ø2yìÝîØ/[Gh>?ô▒ùØ;Þ3¡fÜ¼sK×p ├Û{K³╬äH

                IBM037, 	!0#.a)a°ÊN¡cd±òÈaaëÍH¦öñÖx95¬üÏ¶Z+
                @°¥û0F® ¬* Þ#XeØºíUdï­ÖCÎðªòü?ÖÜº`ýÒð¸$åÇl£p¸YÝÃªÐË.Æø C²#.Üódç

                IBM1026, 	Ğ0Ö.a)a°ÊN¡cd±òÈaaëÍHş~ñ#x95^\Ï¶Z+
                Ş°¥û0F® ^* @ÖXeØºíUdï­#CÎ}ªò\?#"ºı`Ò}¸İå[l£p¸Y$Ãª]Ë.Æø C²Ö."ód{

                IBM1047, 	!0#.a)a°ÊN¡cd±òÈaaëÍH¦öñÖx95^üÏ¶Z+
                @°¥û0F® ^* Þ#XeØºíUdï­ÖCÎðªòü?ÖÜº`ýÒð¸$åÇl£p¸Y[ÃªÐË.Æø C²#.Üódç

                IBM273, 	Ü0#.a)a°ÊN¡cd±òÈaaëÍHö¦ñ\x95^}Ï¶Z+
                §°¥û0F® ^* Þ#XeØºíUdï­\CÎðªò}?\]º`ýÒð¸$åÇl£p¸YÝÃªÐË.Æø C²#.]ódç

                IBM277, 	¤0Æ.a)a°ÊN¡cd±òÈaaëÍHøöñÖx95^~Ï¶Z+
                Ø°¥û0F® ^* ÞÆXe@ºíUdï­ÖCÎðªò~?ÖÜº`ýÒð¸Å}Çl£p¸YÝÃªÐË.[¦ C²Æ.Üódç

                IBM278, 	¤0Ä.a)a°ÊN¡cd±òÈaaëÍHö¦ñ@x95^~Ï¶Z+
                Ö°¥û0F® ^* ÞÄXeØºíUdï­@CÎðªò~?@ÜºéýÒð¸Å}Çl£p¸YÝÃªÐË.Æø C²Ä.Üódç

                IBM280, 	é0£.a)a[ÊN¡cd±¦ÈaaëÍHòöñÖx95^üÏ¶Z+
                §[¥û0F® ^* Þ£XeØºíUdï­ÖCÎðª¦ü?ÖÜºùýÒð¸$åÇl#p¸YÝÃªÐË.Æø C²£.Üód\

                IBM284, 	]0Ñ.a)a°ÊN¡cd±òÈaaëÍHñö¦Öx95¬üÏ¶Z+
                @°¥û0F® ¬* ÞÑXeØºíUdï­ÖCÎðªòü?ÖÜº`ýÒð¸$åÇl£p¸YÝÃªÐË.Æø C²Ñ.Üódç

                IBM285, 	!0#.a)a°ÊN¡cd±òÈaaëÍH¦öñÖx95¬üÏ¶Z+
                @°¥û0F® ¬* Þ#XeØºíUdï­ÖCÎðªòü?ÖÜº`ýÒð¸£åÇl[p¸YÝÃªÐË.Æø C²#.Üódç

                IBM297, 	§0£.a)a[ÊN¡cd±òÈaaëÍHùöñÖx95^üÏ¶Z+
                à[¥û0F® ^* Þ£XeØºíUdï­ÖCÎðªòü?ÖÜºµýÒð¸$åÇl#p¸YÝÃªÐË.Æø C²£.Üód\

                IBM437, 	Z4≡{Kü]üÉr<(╒¬âäÅ═t6üü?'Su╚j╠I∞º∙⌡
                _▄#w╢ΘN|É▓█≡╞»@_\A«{τ1àÇ¢UΣäW╩∞├v"îÜ═▄o∞ⁿ¢2yìφî¥/[Gh>?ô▒ù¥;Φ3¡fÜ¼sK₧p ├Ω{Kⁿ╬äH

                IBM500, 	]0#.a)a°ÊN¡cd±òÈaaëÍH¦öñÖx95^üÏ¶Z+
                @°¥û0F® ^* Þ#XeØºíUdï­ÖCÎðªòü?ÖÜº`ýÒð¸$åÇl£p¸YÝÃªÐË.Æø C²#.Üódç

                IBM775, 	Z4­{Kü]üÉr<(š¬āäÅ═t6üü?'Su╚j╠Iņ¦∙§
                _▄#wČķN|É▓█­Ų»@_\A«{ń1ģĆøUõäW╩ņ├v"īÜ═▄oņ³ø2yŹĒīØ/[Gh>?ō▒ŚØ;Ķ3ŁfÜ¼sK×p ├Ļ{K³╬äH

                IBM850, 	Z4­{Kü]üÉr<(ı¬âäÅ═t6üü?'Su╚j╠Iýº¨§
                _▄#wÂÚN|É▓█­ã»@_\A«{þ1àÇøUõäW╩ý├v"îÜ═▄oý³ø2yìÝîØ/[Gh>?ô▒ùØ;Þ3¡fÜ¼sK×p ├Û{K³╬äH

                IBM852, 	Z4­{Kü]üÉr<(Ň¬âäĆ═t6üü?'Su╚j╠Iýž¨§
                _▄#wÂÚN|É▓█­Ă»@_\A«{š1ůÇŤUńäW╩ý├v"îÜ═▄oýŘŤ2yŹÝîŁ/[Gh>?ô▒ŚŁ;Ŕ3şfÜČsK×p ├ŕ{KŘ╬äH

                IBM855, 	Z4­{KЂ]Ђљr<(НфЃёЈ═t6ЂЂ?'Su╚j╠IВДщш
                _▄#wХжN|љ▓█­к»@_\A«{у1ЁђЏUСёW╩В├v"їџ═▄oВЧЏ2yЇьїЮ/[Gh>?Њ▒ЌЮ;У3ГfџгsKъp ├Ж{KЧ╬ёH

                IBM860, 	Z4≡{Kü]üÉr<(╒¬âãÂ═t6üü?'Su╚j╠I∞º∙⌡
                _▄#w╢ΘN|É▓█≡╞»@_\A«{τ1àÇ¢UΣãW╩∞├v"ÔÜ═▄o∞ⁿ¢2yìφÔÙ/[Gh>?ô▒ùÙ;Φ3¡fÜ¼sK₧p ├Ω{Kⁿ╬ãH

                IBM861, 	Z4≡{Kü]üÉr<(╒¬âäÅ═t6üü?'Su╚j╠I∞Ú∙⌡
                _▄#w╢ΘN|É▓█≡╞»@_\A«{τ1àÇøUΣäW╩∞├v"ðÜ═▄o∞ⁿø2yÞφðØ/[Gh>?ô▒ÝØ;Φ3¡fÜ¼sK₧p ├Ω{Kⁿ╬äH

                IBM862, 	Z4≡{Kב]בנr<(╒¬דהן═t6בב?'Su╚j╠I∞º∙⌡
                _▄#w╢ΘN|נ▓█≡╞»@_\A«{τ1וא¢UΣהW╩∞├v"לת═▄o∞ⁿ¢2yםφל¥/[Gh>?ף▒ק¥;Φ3¡fת¼sK₧p ├Ω{Kⁿ╬הH

                IBM863, 	Z4≡{Kü]üÉr<(╒¬âÂ§═t6üü?'Su╚j╠I∞¯∙⌡
                _▄#w╢ΘN|É▓█≡╞»@_\A«{τ1àÇ¢UΣÂW╩∞├v"îÜ═▄o∞ⁿ¢2y‗φîÙ/[Gh>?ô▒ùÙ;Φ3¾fÜ¼sKÛp ├Ω{Kⁿ╬ÂH

                IBM865, 	Z4≡{Kü]üÉr<(╒¬âäÅ═t6üü?'Su╚j╠I∞º∙⌡
                _▄#w╢ΘN|É▓█≡╞¤@_\A«{τ1àÇøUΣäW╩∞├v"îÜ═▄o∞ⁿø2yìφîØ/[Gh>?ô▒ùØ;Φ3¡fÜ¼sK₧p ├Ω{Kⁿ╬äH

                IBM866, 	Z4Ё{KБ]БРr<(╒кГДП═t6ББ?'Su╚j╠Iьз∙ї
                _▄#w╢щN|Р▓█Ё╞п@_\Aо{ч1ЕАЫUфДW╩ь├v"МЪ═▄oь№Ы2yНэМЭ/[Gh>?У▒ЧЭ;ш3нfЪмsKЮp ├ъ{K№╬ДH

                IBM868, 	Z4­{K۱]۱r<(ﻐﮊ۳۴ﺎ═t6۱۱?'Su╚j╠Iﻭﺭﮰﺋ
                _▄#wﺹﻥN|▓█­ﻇ»@_\A«{ﻣ1۵۰ﺛUﻟ۴W╩ﻭ├v"؟ﺙ═▄oﻭﹽﺛ2yﺁﮦ؟ﺟ/[Gh>?ﭖ▒ﺗﺟ;ﮞ3ﺵfﺙﺳsKﭺp ├ﻧ{Kﹽ╬۴H

                IBM870, 	]0#.a)a°ĘNŚcdşŕŮaaëÍH|öćÖx95^üĽžZ+
                @°żű0FŞ ^* Ř#Xe˘ńíUdľ­ÖCÎđłŕü?ÖÜń`ýŔđ¸$čÇlĄp¸YÝĂłĐË.˛ˇ Cď#.Üódç

                IBM871, 	Æ0#.a)a°ÊN¡cd±òÈaaëÍH¦~ñ^x95ÖüÏ¶Z+
                Ð°¥û0F® Ö* [#XeØºíUdï­^CÎ`ªòü?^ÜºðýÒ`¸$åÇl£p¸YÝÃª@Ë.]ø C²#.Üódç

                IBM918, 	]0#.a)aﮊ۲Nﺿcdﺯﻣ۴aaﭘ۵H`ﻡﺏﺊx95^ﻭ۷ﻓZ+
                @ﮊﻎﺅ0Fﻋ ^* ﻊ#XeﺧﺳﺕUdﭦ­ﺊC۶ﺫﺱﻣﻭ?ﺊﮮﺳ۹ﺭﺋﺫﺷ$ﺎﺣlﻍpﺷYﻉﭼﺱﻇ۳.ﺹ۰ Cﺀ#.ﮮﮞd

                ISO-8859-1, 	Z4ð{K]r<(ÕªÍt6?'SuÈjÌIì§ùõ
                _Ü#w¶éN|²ÛðÆ¯@_\A®{ç1UäWÊìÃv"ÍÜoìü2yí/[Gh>?±;è3­f¬sKp Ãê{KüÎH

                ISO-8859-13, 	Z4š{K]r<(ÕŖĶt6?'SuČjĢIģ§łõ
                _Ü#w¶éN|²ŪšĘÆ@_\A®{ē1UäWŹģĆv"ĶÜoģü2yķ/[Gh>?±;č3­f¬sKp Ćź{KüĪH

                ISO-8859-15, 	Z4ð{K]r<(ÕªÍt6?'SuÈjÌIì§ùõ
                _Ü#w¶éN|²ÛðÆ¯@_\A®{ç1UäWÊìÃv"ÍÜoìü2yí/[Gh>?±;è3­f¬sKp Ãê{KüÎH

                ISO-8859-2, 	Z4đ{K]r<(ŐŞÍt6?'SuČjĚIě§ůő
                _Ü#wśéN|˛ŰđĆŻ@_\AŽ{ç1UäWĘěĂv"ÍÜoěü2yí/[Gh>?ą;č3­fŹsKp Ăę{KüÎH

                ISO-8859-4, 	Z4đ{K]r<(ÕĒÍt6?'SuČjĖIė§ųõ
                _Ü#wļéN|˛ÛđÆ¯@_\AŽ{į1UäWĘėÃv"ÍÜoėü2yí/[Gh>?ą;č3­fŦsKp Ãę{KüÎH

                ISO-8859-5, 	Z4№{K]r<(еЊЭt6?'SuШjЬIьЇљѕ
                _м#wЖщN|Вл№ЦЏ@_\AЎ{ч1UфWЪьУv"Эмoьќ2yэ/[Gh>?Б;ш3­fЌsKp Уъ{KќЮH

                ISO-8859-9, 	Z4ğ{K]r<(ÕªÍt6?'SuÈjÌIì§ùõ
                _Ü#w¶éN|²ÛğÆ¯@_\A®{ç1UäWÊìÃv"ÍÜoìü2yí/[Gh>?±;è3­f¬sKp Ãê{KüÎH

                KOI8-R, 	Z4П{K│]│░r<(у╙┐└▐мt6││?'SuхjлIЛ╖ЫУ
                _э#w╤ИN|░╡шПф╞@_\A╝{Г1┘─⌡UД└WйЛцv"▄ мэoЛЭ⌡2y█М▄²/[Gh>?⌠╠≈²;Х3╜f ╛sK·p цЙ{KЭн└H

                KOI8-U, 	Z4П{K│]│░r<(у╙┐└▐мt6││?'SuхjлIЛїЫУ
                _э#wІИN|░╡шПф╞@_\A╝{Г1┘─⌡UД└WйЛцv"▄ мэoЛЭ⌡2y█М▄²/[Gh>?⌠╠≈²;Х3ґf ╛sK·p цЙ{KЭн└H

                UTF-16BE, 	娇㓰ሑ筋ཱྀ嶁遲㰨픏ꨆ莄迍琶膁㼆❓痈櫌䧬ꟹ応⍷뛩丕粐닛꽀彜ᝁή篧ㆅ肛嗤著쫬썶⊌髍ǜ濬ﲛ㉹跭貝⽛䝨㸿鎱鞝㯨㎭暚걳ɋ鹰쏪ᵻ䯼Ꮞ葈

                windows-1251, 	Z4р{KЃ]Ѓђr<(ХЄѓ„ЏНt6ЃЃ?'SuИjМIм§щх
                _Ь#w¶йN|ђІЫрЖЇ@_\A®{з1…Ђ›Uд„WКмГv"ЊљНЬoмь›2yЌнЊќ/[Gh>?“±—ќ;и3­fљ¬sKћp Гк{KьО„H

                windows-1256, 	Z4ً{Kپ]پگr<(صھƒ„ڈحt6پپ?'SuبjجIى§ùُ
                _ـ#w¶éN|گ²غًئ¯@_\A®{ç1…€›Uن„Wتىأv"Œڑحـoىü›2yچيŒ‌/[Gh>?“±—‌;è3­fڑ¬sK‍p أê{Küخ„H

                x-IBM1006, 	Z4ﻭ{K]r<(ﻃ۹ﺱt6?'SuﺫjﮊIﮞ۶ﯼﺀ
                _ﻎ#wﭘﻠN|ﻍﻭﺩﺁ@_\A؟{ﻝ1UﻛWﮌﮞﺣv"ﺱﻎoﮞﮰ2yﻥ/[Gh>?ﺎ;ﻟ3­f؛sKp ﺣﻡ{KﮰﺳH

                x-IBM1025, 	]0#.a)aйЌNрcdиКЎaaћЏH|ЙјЖx95^ПачZ+
                @йзО0Fв ^* ж#XeцлўUdЪХЖCюфкКП?ЖЭл`гВфн$іЈlыpнYуІкт­.оЊ CТ#.ЭЛdї

                x-IBM1046, 	Z4ﹺ{K×]×ﹹr<(ﺹﺟ┘ﺡt6××?'SuﺏjﺝIﹲﺑﻹﻟ
                _ﺂ#w٦ﻯN|ﹹ٢ﻌﹺﺉﺳ@_\Aﺧ{ﻫ1ﺈﻐUﻝWﺕﹲﺃv"┐ﻏﺡﺂoﹲﻧﻐ2y┌ﹴ┐ﻸ/[Gh>?ﹿ١ﻳﻸ;ﻭ3­fﻏ،sKﻺp ﺃﻱ{KﻧﺥH

                x-IBM1097, 	!0#.a)aﺹﺣNﻉcdﺷﻧﺧaaﺄﺩH؛ﻥ۱x95¬ﮤﺭﮎZ+@ﺹﻑﻬ0Fﻎ ¬* ﻍ#XeﮊﺽﺅUdﺏ­۱Cﺫﺱﺻﻧﮤ?۱۷ﺽ`ﺳ۲ﺱﻁ$ﺍﺟlﻐpﻁYﻌﺛﺻﻋﺥ.ﻃﭼ Cـ#.۷ﻭdﺎ

                x-IBM1112, 	!0#.a)a°ĘN”cd±ņČaaėŲH¦öžÖx95¬üĻ¶Z+
                @°īć0F® ¬* Ń#XeØŗųUd“­ÖCĪāŖņü?ÖÜŗ`żŅāķ$åĒl£pķYŻŪŖĀĖ.Æø C²#.Üódē

                x-IBM1122, 	¤0Ä.a)a°ÊN¡cd±òÈaaëÍHö¦ñ@x95^~Ï¶Z+
                Ö°¥û0F® ^* ŽÄXeØºíUdï­@CÎšªò~?@ÜºéýÒš¸Å}Çl£p¸YÝÃªŠË.Æø C²Ä.Üódç

                x-IBM1123, 	]0#.a)aйЌNрcdиКЎaaћЏH|ЙјЖx95^ПачZ+
                @йзО0Fв ^* ж#XeцлўUdЪХЖCюфкКП?ЖЭл`гВфн$іЈlыpнYуІкт­.оЊ CТ#.ЭЛdї

                x-IBM1124, 	Z4№{K]r<(еЊЭt6?'SuШjЬIьЇљѕ
                _м#wЖщN|Вл№ЦЏ@_\AЎ{ч1UфWЪьУv"Эмoьќ2yэ/[Gh>?Б;ш3­fЌsKp Уъ{KќЮH

                x-IBM1166, 	]0#.a)aйҮNрcdиКЎaaұҺH|ЙјЖx95^ПачZ+
                @йзО0Fв ^* ж#XeцлўUdЪХЖCюфкКП?ЖЭл`гВфн$іЈlыpнYуІкт­.оӨ CТ#.ЭЛdқ

                x-IBM737, 	Z4Ώ{KΒ]ΒΡr<(╒ςΔΕΠ═t6ΒΒ?'Su╚j╠IΉπ∙Ϋ
                _▄#w╢ώN|Ρ▓█Ώ╞ψ@_\Aχ{ύ1ΖΑδUϊΕW╩Ή├v"Νγ═▄oΉⁿδ2yΞΊΝζ/[Gh>?Υ▒Ωζ;ϋ3φfγυsKηp ├Ά{Kⁿ╬ΕH

                x-IBM921, 	Z4š{K]r<(ÕŖĶt6?'SuČjĢIģ§łõ
                _Ü#w¶éN|²ŪšĘÆ@_\A®{ē1UäWŹģĆv"ĶÜoģü2yķ/[Gh>?±;č3­f¬sKp Ćź{KüĪH

                x-IBM922, 	Z4š{K]r<(ÕªÍt6?'SuÈjÌIì§ùõ
                _Ü#w¶éN|²ÛšÆ‾@_\A®{ç1UäWÊìÃv"ÍÜoìü2yí/[Gh>?±;è3­f¬sKp Ãê{KüÎH

                x-MacCentralEurope, 	Z4ū{KĀ]Āźr<(’™ÉĄŹÕt6ĀĀ?'Su»jŐIžßýű
                _‹#w∂ťN|ź≤Řū∆Į@_\Aģ{Á1ÖÄõUšĄW ž√v"ĆöÕ‹ožŁõ2yćŪĆĚ/[Gh>?ďĪóĚ;Ť3≠fö¨sKěp √Í{KŁőĄH

                x-MacCroatian, 	Z4đ{KÅ]Åêr<(’™ÉÑèÕt6ÅÅ?'SuČjÃIÏßπı
                _‹#w∂ÈN|ê≤¤đĆØ@_\AŽ{Á1ÖÄõU‰ÑW Ï√v"åöÕ‹oÏ¸õ2yçÌåù/[Gh>?ì±óù;č3≠fö¨sKûp √Í{K¸ŒÑH

                x-MacCyrillic, 	Z4р{KБ]БРr<(’™ГДПЌt6ББ?'Su»jћIмІщх
                _№#w∂йN|Р≤џр∆ѓ@_\AЃ{з1ЕАЫUдДW м√v"МЪЌ№oмьЫ2yНнМЭ/[Gh>?У±ЧЭ;и3≠fЪђsKЮp √к{KьќДH

                x-MacGreek, 	Z4π{K¹]¹êr<(’ΣÉ³èΆt6¹¹?'Su»jΧIλßυθ
                _ή#wΕιN|ê≤έπΤ·@_\A°{γ1ÖÄ¦Uδ³W λΟv"¨öΆήoλϋ¦2yçμ¨ù/[Gh>?™±½ù;η3≠fö§sKûp Οξ{KϋΈ³H

                x-MacIceland, 	Z4{KÅ]Åêr<(’™ÉÑèÕt6ÅÅ?'Su»jÃIÏß˘ı
                _Ð#w∂ÈN|ê≤¤∆Ø@_\AÆ{Á1ÖÄõU‰ÑW Ï√v"åöÕÐoÏ¸õ2yçÌåù/[Gh>?ì±óù;Ë3≠fö¨sKûp √Í{K¸ŒÑH

                x-MacRoman, 	Z4{KÅ]Åêr<(’™ÉÑèÕt6ÅÅ?'Su»jÃIÏß˘ı
                _‹#w∂ÈN|ê≤€∆Ø@_\AÆ{Á1ÖÄõU‰ÑW Ï√v"åöÕ‹oÏ¸õ2yçÌåù/[Gh>?ì±óù;Ë3≠fö¨sKûp √Í{K¸ŒÑH

                x-MacRomania, 	Z4{KÅ]Åêr<(’™ÉÑèÕt6ÅÅ?'Su»jÃIÏß˘ı
                _‹#w∂ÈN|ê≤¤∆Ş@_\AĂ{Á1ÖÄõU‰ÑW Ï√v"åöÕ‹oÏ¸õ2yçÌåù/[Gh>?ì±óù;Ë3≠fö¨sKûp √Í{K¸ŒÑH

                x-MacUkraine, 	Z4р{KБ]БРr<(’™ГДПЌt6ББ?'Su»jћIмІщх
                _№#wґйN|Р≤џр∆ѓ@_\AЃ{з1ЕАЫUдДW м√v"МЪЌ№oмьЫ2yНнМЭ/[Gh>?У±ЧЭ;и3≠fЪђsKЮp √к{KьќДH
             */

}
