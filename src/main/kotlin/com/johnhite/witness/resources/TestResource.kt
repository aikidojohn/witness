package com.johnhite.witness.resources

import org.slf4j.LoggerFactory
import javax.script.*
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.core.Response
import javax.script.ScriptContext.ENGINE_SCOPE
import javax.script.SimpleScriptContext



@Path("/test")
class TestResource {
	private val logger = LoggerFactory.getLogger(javaClass)

	@GET
	fun test() : Response {
		return Response.ok("Test").build()
	}

    @POST
    fun run(script : String) : Response {
        var engine = ScriptEngineManager().getEngineByExtension("kts") as Compilable
        var engine2 = engine as ScriptEngine
        val compiled = engine.compile(script)
        val bindings = SimpleBindings()
        bindings["logger"] = logger

        return Response.ok(compiled.eval(bindings)).build()
       /* with(ScriptEngineManager().getEngineByExtension("kts")) {
            val result = eval(script)
            return Response.ok(result).build()
        }*/
    }
}

open class Foo {
    open var x = 10
}

fun main(args : Array<String>) {
    val logger = Foo()
    //val logger = LoggerFactory.getLogger("test")

    println("from program: " + logger.x)
    var engine = ScriptEngineManager().getEngineByExtension("kts")
    var compiler = engine as Compilable

    val newContext = SimpleScriptContext()
    val bindings = newContext.getBindings(ScriptContext.ENGINE_SCOPE)

    bindings["logger"] = logger
    //engine.put("logger", logger)

    val runnableScript = """
        fun run() {
            println("in runnable");
        }
    """.trimIndent()

    val script = """
        fun run() {
            println("in runnable");
        }
        val logger = bindings["logger"] as com.johnhite.witness.resources.Foo
        println("from script: " + logger.x)
    """.trimIndent()
    //val compiled = compiler.compile(script)
   // println(compiled.eval(newContext))

    val rs = compiler.compile(runnableScript)
    rs.eval()
    val invocable = rs.engine as Invocable
    val runnable = invocable.getInterface(Runnable::class.java)
    runnable.run()
}