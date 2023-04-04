import org.aopalliance.intercept.MethodInterceptor
import org.aopalliance.intercept.MethodInvocation
import org.springframework.aop.framework.ProxyFactory

data class Student(val id: Long, val name: String, val email: String, val age: Int)

class LoggingAdvice: MethodInterceptor {
    override fun invoke(invocation: MethodInvocation): Any? {
        println("--------------------------------------------")
        println("Entered logging advice")
        println("Method name: ${invocation.method.name}")
        println("Method return type: ${invocation.method.returnType}")

        if (invocation.arguments.isNotEmpty()) {
            println("Arguments:")
            var index = 0
            for (item in invocation.arguments) {
                println("Argument no.$index: $item")
                index++
            }
        } else {
            println("No arguments")
        }

        val returnValue = invocation.proceed()
        println("Returned value: $returnValue")

        println("Left logging advice")
        println("--------------------------------------------")
        return returnValue
    }

}

interface StudentService {
    fun addStudent(student: Student)

    fun removeStudentById(id: Long)

    fun getStudents(): List<Student>
}

class StudentServiceImpl: StudentService {
    private val students = ArrayList<Student>()

    override fun addStudent(student: Student) {
        students.add(student)
    }

    override fun removeStudentById(id: Long) {
        students.removeIf { it.id == id }
    }

    override fun getStudents(): List<Student> {
        return students
    }
}

fun main(args: Array<String>) {
    val factory = ProxyFactory(
        StudentServiceImpl()
    )

    factory.addInterface(StudentService::class.java)
    factory.addAdvice(LoggingAdvice())

    val studentProxyService: StudentService = factory.proxy as StudentService

    studentProxyService.addStudent(Student(1, "Test", "test@gmail.com", 22))
    studentProxyService.getStudents()
    studentProxyService.removeStudentById(1)
    studentProxyService.getStudents()
}