package com.yjxxt.rpc.service.impl;

import com.yjxxt.common.result.BaseResult;
import com.yjxxt.rpc.service.ICartService;
import com.yjxxt.rpc.vo.CartResult;
import com.yjxxt.rpc.vo.CartVo;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service(version = "1.0")
public class CartServiceImpl implements ICartService {
    @Resource
    private GoodsMapper goodsMapper;
    @Value("${user.cartKey}")
    private String userCart;
    @Resource(name = "redisTemplate")
    private RedisTemplate<String, Object> redisTemplate;
    @Resource(name = "redisTemplate")
    private HashOperations<String, String, Object> hashOperations;

    @Override
    public BaseResult saveCartVo(CartVo cartVo, Integer userId) {
/**
 * 1.参数校验
 * 商品记录存在性校验
 * 商品数量>=1
 * 2.加入商品到购物车
 * 2.1 购物车不存在该商品
 * put(key,goodsId,CartVo)
 * 2.2 购物车存在该商品
 * 原来商品数量+当前商品数量 存入redis
 *3.返回结果
 */
        Integer goodsId = cartVo.getGoodsId();
        if (null == goodsId || 0 == goodsId) {
            return BaseResult.error("商品记录不存在!");
        }
        GoodsExample example = new GoodsExample();
        example.createCriteria().andGoodsIdEqualTo(goodsId);
        List<Goods> goodsList = goodsMapper.selectByExample(new
                GoodsExample());
        if (CollectionUtils.isEmpty(goodsList)) {
            return BaseResult.error("商品记录不存在!");
        }
        if (cartVo.getGoodsNum() <= 0) {
            return BaseResult.error("商品数量异常!");
        }
// 购物车key
        String key = userCart + ":" + userId;
        CartVo temp = cartVo;
        temp.setAddTime(new Date());
        Map<String, Object> map = hashOperations.entries(key);
        if (map.containsKey(goodsId.toString())) {
// 购物车存在该商品
            temp = (CartVo) map.get(goodsId.toString());
            temp.setGoodsNum(temp.getGoodsNum() + cartVo.getGoodsNum());
            temp.setMarketPrice(cartVo.getMarketPrice());
        }
/**
 * 1.购物车存在当前商品 修改商品
 * 2.购物车不存在当前商品 添加新商品
 */
        hashOperations.put(key, goodsId.toString(), temp);
        return BaseResult.success();
    }

    /**
     * 更新购物车指定商品数量
     *
     * @param cartVo
     * @param userId
     * @return
     */
    @Override
    public BaseResult updateCartVo(CartVo cartVo, Integer userId) {
/**
 * 1.参数校验
 * 商品记录存在校验
 * 数量>0
 * 2.更新商品数量
 * 根据商品id 更新value
 * 3.返回结果
 */
        Integer goodsId = cartVo.getGoodsId();
        if (null == goodsId || 0 == goodsId) {
            return BaseResult.error("商品记录不存在!");
        }
        GoodsExample example = new GoodsExample();
        example.createCriteria().andGoodsIdEqualTo(goodsId);
        List<Goods> goodsList = goodsMapper.selectByExample(new
                GoodsExample());
        if (CollectionUtils.isEmpty(goodsList)) {
            return BaseResult.error("商品记录不存在!");
        }
        cartVo.setAddTime(new Date());
// 购物车key
        String key = userCart + ":" + userId;
        hashOperations.put(key, goodsId.toString(), cartVo);
        return BaseResult.success();
    }

    @Override
    public BaseResult delCartVo(Integer goodsId, Integer userId) {
        if (null == goodsId || 0 == goodsId) {
            return BaseResult.error("商品记录不存在!");
        }
        GoodsExample example = new GoodsExample();
        example.createCriteria().andGoodsIdEqualTo(goodsId);
        List<Goods> goodsList = goodsMapper.selectByExample(new
                GoodsExample());
        if (CollectionUtils.isEmpty(goodsList)) {
            return BaseResult.error("商品记录不存在!");
        }
        String key = userCart + ":" + userId;
        hashOperations.delete(key, goodsId.toString());
        return BaseResult.success();
    }

    @Override
    public BaseResult clearCart(Integer userId) {
        redisTemplate.delete(userCart + ":" + userId);
        return BaseResult.success();
    }

    @Override
    public Integer countGoodsNum(Integer userId) {
        return hashOperations.values(userCart + ":" + userId)
                .stream().mapToInt(c -> {
                    CartVo cartVo = (CartVo) c;
                    return cartVo.getGoodsNum();
                }).sum();
    }

    @Override
    public CartResult getCartListInfo(Integer userId) {
        CartResult result = new CartResult();
        String key = userCart + ":" + userId;
        if (redisTemplate.hasKey(key)) {
            List<CartVo> cartVoList = new ArrayList<CartVo>();
            Map<String, Object> map = hashOperations.entries(key);
            BigDecimal total = BigDecimal.ZERO;
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                CartVo cartVo = (CartVo) entry.getValue();
                5. 门户系统集成购物车
                5.1.门户系统购物车方法实现
                shop - portal的CartController.java
                total = total.add(cartVo.getMarketPrice().multiply(BigDecimal.valueOf(cartVo
                        .getGoodsNum())));
                cartVoList.add(cartVo);
            }
// 商品列表
            result.setCartList(cartVoList);
// 商品总金额
            result.setTotalPrice(total);
// 商品总件数
            result.setCount(this.countGoodsNum(userId));
        }
        return result;
    }
}