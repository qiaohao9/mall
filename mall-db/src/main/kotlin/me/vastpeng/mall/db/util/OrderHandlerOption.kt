package me.vastpeng.mall.db.util


class OrderHandlerOption {
    var cancel: Boolean = false
    var delete: Boolean = false
    var pay: Boolean = false
    var comment: Boolean = false
    var confirm: Boolean = false
    var refund: Boolean = false
    var rebuy: Boolean = false

    fun isCancel(): Boolean = this.cancel
    fun isDelete(): Boolean = this.delete
    fun isPay(): Boolean = this.pay
    fun isComment(): Boolean = this.comment
    fun isConfirm(): Boolean = this.confirm
    fun isRefund(): Boolean = this.refund
    fun isRebuy(): Boolean = this.rebuy

}