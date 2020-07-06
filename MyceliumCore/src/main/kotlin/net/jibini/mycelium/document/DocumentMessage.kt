package net.jibini.mycelium.document

import net.jibini.mycelium.document.impl.DocumentProxy
import net.jibini.mycelium.transfer.Addressed
import net.jibini.mycelium.transfer.impl.JSONAddressedImpl
import org.json.JSONObject

class DocumentMessage(val internal : JSONObject) : Addressed by JSONAddressedImpl(internal)
{
    inline fun <reified T> build() : T = build(T::class.java)

    fun <T> build(interfaceClass : Class<T>) : T = DocumentProxy.createFromJSON(interfaceClass, internal)

    override fun toString(): String = internal.toString()
}