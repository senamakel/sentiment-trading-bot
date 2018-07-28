package io.cryptocontrol.whitebird;

import com.esotericsoftware.yamlbeans.YamlReader;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.util.Map;

/**
 * @author enamakel@cryptocontrol.io
 */
@Data
public class Parameters {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    // The demo mode will show the spreads but won't actually trade anything
    Boolean demo = true;

    // The spread threshold above which the trailing spreads are generated to capture an arbitrage opportunity
    Double spreadEntry = 0.0080;

    // The maximum difference between the target limit price and the computed limit price of an order. That is the
    // price generated by looking at the current liquidity in the order books. If the difference is greater than
    // priceDeltaLimit then no trades will be generated because there is not enough liquidity (risk of slippage)
    Double priceDeltaLimit = 0.10;

    // The limit under which the trailing spread is generated. If the current spread is above SpreadTarget and at
    // 0.70%, then by default the trailing spread will be generated at 0.62%
    Double trailingSpreadLim = 0.0008;

    // The number of times the spread must be between SpreadTarget and the trailing spread before sending the
    // orders to the market
    Integer trailingSpreadCount = 5;

    // In order to be executed as fast as possible and avoid slippage, Whitebird checks the liquidity in the order
    // books of the exchanges and makes sure there is at least 3.0 times the needed liquidity before executing the order
    Double orderBookFactor = 3.0;

    // If true, display the spreads volatility information in the log file. This is not used for the moment and only
    // displayed as information
    Boolean useVolatility = false;

    // The period length of the volatility in number of iterations. This is not used for the moment and only
    // displayed as information
    Integer volatilityPeriod = 600;

    // This is the targeted profit. It represents the net profit and takes the exchange fees into account. If
    // spreadEntry is at 0.80% and trades are generated at that level on two exchanges with 0.25% fees each,
    // Whitebird will set the exit threshold at -0.70% (0.80% spread entry - 4x0.25% fees - 0.50% target = -0.70%)
    Double spreadTarget = 0.0050;

    // Time lapse in seconds of an iteration. By default the quotes download and the spreads analysis for all the
    // exchanges are done every 3 seconds
    Double interval = 3.0;

    // The maximum number of iteration. Once debugMaxIteration is reached Whitebird is terminated. Useful
    // for troubleshooting the software
    Integer debugMaxIteration = 3200000;

    // When true, all the Leg2 exposure available on your accounts will be used. Otherwise, the amount defined by
    // testedExposure will be used. Note: the cash used for a trade will be the minimum of the two exchanges, minus
    // 1.00% as a small margin: if there is $1,000 on the first account and $1,100 on the second one, $990 will be used
    // on each exchange, i.e. $1,000 - (1% * $1,000). The exposure is $1,980
    Boolean useFullExposure = false;

    // If useFullExposure is false, that parameter defines the USD amount that will be used. The minimum has to be $5
    // otherwise some exchanges might reject the orders
    Double testedExposure = 5.00;

    // Maximum exposure per exchange. If the limit is $25,000 then Whitebird won't send any order larger than that on
    // each exchange
    Double maxExposure = 25000.00;

    // The maximum length of a trade in number of iterations. If this value is reached then Whitebird will exit the
    // market regardless of the spread. Warning: with this value the system can exit with a loss so It's recommended
    // to use a large value. The default is 180 days with GapSec at 3 seconds
    Integer maxTradeIterations = 5184000;

    String bitfinexKey;
    String bitfinexSecret;
    Double bitfinexTradingFees = 0.0020;
    Double bitfinexWithdrawlFees = 0.0020;
    Double bitfinexDepositFees = 0.0020;
    Boolean bitfinexEnable = false;

    String okcoinApi;
    String okcoinSecret;
    Double okcoinTradingFees = 0.0020;
    Double okcoinWithdrawlFees = 0.0020;
    Double okcoinDepositFees = 0.0020;
    Boolean okcoinEnable = false;

    String bitstampClientId;
    String bitstampApi;
    String bitstampSecret;
    Double bitstampFees;
    Boolean bitstampEnable = false;

    String geminiApi;
    String geminiSecret;
    Double geminiFees;
    Boolean geminiEnable = false;

    String krakenApi;
    String krakenSecret;
    Double krakenFees;
    Boolean krakenEnable = false;

    String itbitApi;
    String itbitSecret;
    Double itbitFees;
    Boolean itbitEnable = false;

    String btceApi;
    String btceSecret;
    Double btceFees;
    Boolean btceEnable = false;

    String poloniexApi;
    String poloniexSecret;
    Double poloniexFees;
    Boolean poloniexEnable = false;

    String gdaxApi;
    String gdaxSecret;
    Double gdaxFees;
    Boolean gdaxEnable = false;

    String quadrigaApi;
    String quadrigaSecret;
    String quadrigaClientId;
    Double quadrigaFees;
    Boolean quadrigaEnable = false;


    public static Parameters readFromFile(String filename) throws Exception {
        Parameters parameters = new Parameters();

        logger.info("spread entry: " + parameters.spreadEntry);
        logger.info("spread entry target: " + parameters.spreadTarget);

        YamlReader reader = new YamlReader(new FileReader(filename));
        Object object = reader.read();
        Map map = (Map) object;
        Map exchangesMap = (Map) map.get("exchanges");

        if (exchangesMap != null) {
            Map bitfinexMap = (Map) exchangesMap.get("bitfinex");

            if (bitfinexMap != null) {
                parameters.bitfinexEnable = bitfinexMap.get("enabled").equals("true");
                parameters.bitfinexKey = (String) bitfinexMap.get("key");
                parameters.bitfinexSecret = (String) bitfinexMap.get("secret");
            }
        }

        // Do some verifications about the parameters
        if (!parameters.demo) {
            if (!parameters.useFullExposure) {
                if (parameters.testedExposure < 10.0 /*&& parameters.leg2.equals("USD")*/) {
                    // TODO do the same check for other currencies. Is there a limit?
                    throw new Exception("Minimum USD needed: $10.00! Otherwise some exchanges will reject the orders");
                }

                if (parameters.testedExposure > parameters.maxExposure) {
                    throw new Exception(String.format("Test exposure (\"%.2f\") is above max exposure (\"%.2f\"",
                            parameters.testedExposure, parameters.maxExposure));
                }
            }
        }

        // We only trade BTC/USD for the moment
//        if (!parameters.leg1.equals("BTC") || !parameters.leg2.equals("USD")) {
//            throw new Exception("Valid currency pair is only BTC/USD for now");
//        }

        return parameters;
    }
}
