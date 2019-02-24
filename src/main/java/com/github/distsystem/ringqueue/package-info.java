 
/**
 * 环形队列，为解决高并发环境下，有延时任务的触发，例如，经典案例：
 滴滴打车订单完成后，如果用户一直不评价，48小时后会将自动评价为5星。
 *
 */
package com.github.distsystem.ringqueue;