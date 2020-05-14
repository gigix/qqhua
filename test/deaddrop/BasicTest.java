package deaddrop;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class BasicTest {
    @Test
    public void should_demonstrate_usage_of_basic_encoder() {
        String message = "LYSOL, 是利洁时集团的注册商标，主要生产清洁、洗涤制剂及消毒产品. 该商标指定使用商品如下：（包括但不限于下列商品）浸清洁剂的清洁布; 浸有清洁制剂的纸巾; 预润湿浸渍餐具洗涤剂的纸巾; 浸清洁剂的湿巾; 浸化妆水的薄纸; 浸有皮肤清洁液的纸巾; 含洁肤剂纸巾; 浸卸妆液的薄纸; 化妆粉纸; 化妆用除油纸;梳洗用制剂; 肥皂; 研磨剂; 非医用漱口剂; 空气芳香剂; 抛光制剂; 香精油; 香; 个人或动物用除臭剂; 清洁制剂; 清洁制剂（家庭机关用）; 浸药液的薄纸; 一次性消毒湿巾;杀真菌剂; 空气除臭剂; 净化剂; 牲畜用洗涤剂（杀虫剂）; 杀虫剂; 灭菌棉; 消毒剂; 医用营养品; 消毒纸巾; 牙填料; 防腐和消毒清洁品及喷雾剂";

        Basic encoder = new Basic(new String[]{"test/banner.png"});
        encoder.encode_data(message.getBytes());
        encoder.save_images("/tmp");

        Basic decoder = new Basic(new String[]{"/tmp/banner.png"});
        String encryptedData = new String(decoder.decode_data());
        assertThat(encryptedData, is(message));
        System.out.println(encryptedData);
    }
}
