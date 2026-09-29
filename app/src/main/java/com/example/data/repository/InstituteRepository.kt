package com.example.data.repository

import com.example.data.local.InstituteDao
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class InstituteRepository(private val dao: InstituteDao) {

    val allCourses: Flow<List<CourseEntity>> = dao.getAllCourses()
    val enrolledCourses: Flow<List<CourseEntity>> = dao.getEnrolledCourses()
    val allLiveClasses: Flow<List<LiveClassEntity>> = dao.getAllLiveClasses()
    val allRecordedLectures: Flow<List<RecordedLectureEntity>> = dao.getAllRecordedLectures()
    val allStudyMaterials: Flow<List<StudyMaterialEntity>> = dao.getAllStudyMaterials()
    val allTests: Flow<List<TestExamEntity>> = dao.getAllTests()
    val allAssignments: Flow<List<AssignmentEntity>> = dao.getAllAssignments()
    val allDoubts: Flow<List<DoubtEntity>> = dao.getAllDoubts()
    val allAttendance: Flow<List<AttendanceRecordEntity>> = dao.getAllAttendanceRecords()
    val allNotifications: Flow<List<NotificationEntity>> = dao.getAllNotifications()
    val allPayments: Flow<List<PaymentEntity>> = dao.getAllPayments()
    val allStudents: Flow<List<UserEntity>> = dao.getAllStudents()
    val allTeachers: Flow<List<UserEntity>> = dao.getAllTeachers()
    val leaderboard: Flow<List<UserEntity>> = dao.getLeaderboard()
    val allReferrals: Flow<List<ReferralRecordEntity>> = dao.getAllReferrals()

    suspend fun initializeSeedDataIfNeeded() {
        val existingCourses = dao.getAllCourses().first()
        if (existingCourses.isNotEmpty()) return

        // 1. Initial Users
        dao.insertUser(
            UserEntity(
                id = "u_std_1",
                role = "STUDENT",
                name = "Aamir Khan",
                email = "student@kgn.edu",
                mobile = "+91 98765 43210",
                passwordHash = "pass123",
                courseOrBatch = "IIT-JEE Super 30 Batch",
                avatar = "std_1",
                referralCode = "KGN-AAMIR-77",
                referralCount = 3,
                walletBonus = 1500.0,
                points = 1850,
                badges = "Top Scorer,Consistent Learner,Streak Master,Olympiad Ranker",
                attendancePercentage = 94.0f,
                streakDays = 14,
                studyMinutes = 540
            )
        )
        dao.insertUser(
            UserEntity(
                id = "u_tch_1",
                role = "TEACHER",
                name = "Prof. Salman Qureshi",
                email = "teacher@kgn.edu",
                mobile = "+91 98111 22334",
                passwordHash = "pass123",
                courseOrBatch = "Physics & Mathematics HOD",
                avatar = "tch_1"
            )
        )
        dao.insertUser(
            UserEntity(
                id = "u_adm_1",
                role = "ADMIN",
                name = "Er. F. K. Ansari",
                email = "admin@kgn.edu",
                mobile = "+91 99000 11223",
                passwordHash = "pass123",
                courseOrBatch = "Director & Academic Dean",
                avatar = "adm_1"
            )
        )

        // Extra students for teacher/admin view & leaderboard
        dao.insertUser(
            UserEntity(
                id = "u_std_2",
                role = "STUDENT",
                name = "Fatima Shaikh",
                email = "fatima@kgn.edu",
                mobile = "+91 98222 33445",
                passwordHash = "pass123",
                courseOrBatch = "NEET Target Batch",
                referralCode = "KGN-FATIMA-12",
                referralCount = 5,
                walletBonus = 2500.0,
                points = 2150,
                badges = "Top Scorer,NCERT Master,Consistent Learner",
                attendancePercentage = 98.2f,
                streakDays = 21,
                studyMinutes = 720
            )
        )
        dao.insertUser(
            UserEntity(
                id = "u_std_3",
                role = "STUDENT",
                name = "Rohan Sharma",
                email = "rohan@kgn.edu",
                mobile = "+91 97333 44556",
                passwordHash = "pass123",
                courseOrBatch = "Class 12th CBSE Science",
                referralCode = "KGN-ROHAN-44",
                referralCount = 1,
                walletBonus = 500.0,
                points = 1420,
                badges = "Consistent Learner,Doubt Solver",
                attendancePercentage = 88.5f,
                streakDays = 6,
                studyMinutes = 310
            )
        )
        dao.insertUser(
            UserEntity(
                id = "u_std_4",
                role = "STUDENT",
                name = "Zaid Siddiqui",
                email = "zaid@kgn.edu",
                mobile = "+91 96123 45678",
                passwordHash = "pass123",
                courseOrBatch = "IIT-JEE Super 30 Batch",
                referralCode = "KGN-ZAID-99",
                referralCount = 2,
                walletBonus = 1000.0,
                points = 1680,
                badges = "Top Scorer,Calculus Wizard",
                attendancePercentage = 91.0f,
                streakDays = 9,
                studyMinutes = 480
            )
        )

        // Seed Referrals
        val sampleReferrals = listOf(
            ReferralRecordEntity(
                id = "ref_1",
                referrerId = "u_std_1",
                referrerName = "Aamir Khan",
                referralCode = "KGN-AAMIR-77",
                referredUserName = "Suhail Merchant",
                referredUserEmail = "suhail@gmail.com",
                bonusAmount = 500.0,
                discountGiven = 1000.0,
                date = "22 Sep 2026"
            ),
            ReferralRecordEntity(
                id = "ref_2",
                referrerId = "u_std_1",
                referrerName = "Aamir Khan",
                referralCode = "KGN-AAMIR-77",
                referredUserName = "Danish Patel",
                referredUserEmail = "danish@gmail.com",
                bonusAmount = 500.0,
                discountGiven = 1000.0,
                date = "24 Sep 2026"
            ),
            ReferralRecordEntity(
                id = "ref_3",
                referrerId = "u_std_2",
                referrerName = "Fatima Shaikh",
                referralCode = "KGN-FATIMA-12",
                referredUserName = "Alia Mansoori",
                referredUserEmail = "alia@gmail.com",
                bonusAmount = 500.0,
                discountGiven = 900.0,
                date = "25 Sep 2026"
            )
        )
        dao.insertReferrals(sampleReferrals)

        // 2. Courses
        val courses = listOf(
            CourseEntity(
                id = "c_jee",
                title = "IIT-JEE 2026 Achiever Batch (PCM)",
                category = "IIT-JEE",
                description = "Complete syllabus coverage for JEE Mains & Advanced with daily live classes, DPPs, doubt sessions and national test series.",
                instructorName = "Prof. Salman & Team KGN",
                price = 4999.0,
                originalPrice = 14999.0,
                duration = "12 Months",
                validityMonths = 18,
                totalLectures = 240,
                totalNotes = 85,
                totalTests = 45,
                isPopular = true,
                isEnrolled = true
            ),
            CourseEntity(
                id = "c_neet",
                title = "NEET 2026 Rankers Batch (PCB)",
                category = "NEET-UG",
                description = "NCERT-aligned medical entrance preparation covering Biology, Physics, and Chemistry with 5000+ MCQs and mock tests.",
                instructorName = "Dr. Yasmin Siddiqui & KGN Faculty",
                price = 4499.0,
                originalPrice = 12999.0,
                duration = "12 Months",
                validityMonths = 18,
                totalLectures = 210,
                totalNotes = 95,
                totalTests = 50,
                isPopular = true,
                isEnrolled = false
            ),
            CourseEntity(
                id = "c_12",
                title = "Class 12th Board Booster (Science)",
                category = "Class 12th",
                description = "Target 95%+ in CBSE & State Boards. Detailed derivations, solved numericals, formula sheets and sample papers.",
                instructorName = "Er. F. K. Ansari & Team",
                price = 2999.0,
                originalPrice = 7999.0,
                duration = "8 Months",
                validityMonths = 12,
                totalLectures = 150,
                totalNotes = 60,
                totalTests = 30,
                isPopular = false,
                isEnrolled = true
            ),
            CourseEntity(
                id = "c_10",
                title = "Class 10th Board Foundation & NTSE",
                category = "Class 10th",
                description = "Solid foundation in Mathematics, Science and Social Studies for board top ranks and Olympiad success.",
                instructorName = "Prof. Imran & Dr. S. K. Ali",
                price = 1999.0,
                originalPrice = 5999.0,
                duration = "10 Months",
                validityMonths = 12,
                totalLectures = 120,
                totalNotes = 45,
                totalTests = 25,
                isPopular = true,
                isEnrolled = false
            ),
            CourseEntity(
                id = "c_found",
                title = "Junior STEM Olympiad (Class 8th-9th)",
                category = "Foundation",
                description = "Conceptual clarity and analytical problem-solving skills for JEE/NEET pre-foundation and NTSE.",
                instructorName = "KGN Junior Faculty Wing",
                price = 1499.0,
                originalPrice = 4999.0,
                duration = "6 Months",
                validityMonths = 10,
                totalLectures = 90,
                totalNotes = 35,
                totalTests = 20,
                isPopular = false,
                isEnrolled = false
            )
        )
        dao.insertCourses(courses)

        // 3. Live Classes
        val liveClasses = listOf(
            LiveClassEntity(
                id = "live_1",
                title = "Rotational Mechanics: Moment of Inertia & Torque",
                subject = "Physics",
                courseId = "c_jee",
                teacherName = "Prof. Salman Qureshi",
                date = "Today",
                time = "06:00 PM - 07:30 PM",
                status = "LIVE_NOW",
                roomKey = "KGN-PHY-LIVE-88",
                participantCount = 64,
                isReminderSet = true
            ),
            LiveClassEntity(
                id = "live_2",
                title = "Electrochemical Cells & Nernst Equation",
                subject = "Chemistry",
                courseId = "c_jee",
                teacherName = "Dr. R. K. Varma",
                date = "Tomorrow",
                time = "10:00 AM - 11:30 AM",
                status = "UPCOMING",
                roomKey = "KGN-CHM-UPC-12",
                participantCount = 0,
                isReminderSet = false
            ),
            LiveClassEntity(
                id = "live_3",
                title = "Definite Integrals & Area Under Curves",
                subject = "Mathematics",
                courseId = "c_jee",
                teacherName = "Er. F. K. Ansari",
                date = "Tomorrow",
                time = "04:30 PM - 06:00 PM",
                status = "UPCOMING",
                roomKey = "KGN-MTH-UPC-90",
                participantCount = 0,
                isReminderSet = true
            ),
            LiveClassEntity(
                id = "live_4",
                title = "Human Reproduction & Embryology - NCERT Deep Dive",
                subject = "Biology",
                courseId = "c_neet",
                teacherName = "Dr. Yasmin Siddiqui",
                date = "Yesterday",
                time = "05:00 PM - 06:30 PM",
                status = "COMPLETED",
                roomKey = "KGN-BIO-CMP-04",
                participantCount = 92,
                isReminderSet = false
            )
        )
        dao.insertLiveClasses(liveClasses)

        // 4. Recorded Lectures
        val recorded = listOf(
            RecordedLectureEntity(
                id = "rec_1",
                courseId = "c_jee",
                subject = "Physics",
                chapterName = "Rotational Motion",
                title = "Lecture 01: Center of Mass & System of Particles",
                durationMinutes = 58,
                teacherName = "Prof. Salman Qureshi",
                watchProgressPercent = 1.0f,
                isCompleted = true,
                viewsCount = 340
            ),
            RecordedLectureEntity(
                id = "rec_2",
                courseId = "c_jee",
                subject = "Physics",
                chapterName = "Rotational Motion",
                title = "Lecture 02: Moment of Inertia of Standard Bodies",
                durationMinutes = 64,
                teacherName = "Prof. Salman Qureshi",
                watchProgressPercent = 0.65f,
                isCompleted = false,
                viewsCount = 289
            ),
            RecordedLectureEntity(
                id = "rec_3",
                courseId = "c_jee",
                subject = "Mathematics",
                chapterName = "Calculus",
                title = "Lecture 04: Integration by Parts & Partial Fractions",
                durationMinutes = 52,
                teacherName = "Er. F. K. Ansari",
                watchProgressPercent = 0.35f,
                isCompleted = false,
                viewsCount = 412
            ),
            RecordedLectureEntity(
                id = "rec_4",
                courseId = "c_jee",
                subject = "Chemistry",
                chapterName = "Chemical Kinetics",
                title = "Lecture 03: Rate Law, Order & Molecularity",
                durationMinutes = 48,
                teacherName = "Dr. R. K. Varma",
                watchProgressPercent = 0.0f,
                isCompleted = false,
                viewsCount = 195
            ),
            RecordedLectureEntity(
                id = "rec_5",
                courseId = "c_neet",
                subject = "Biology",
                chapterName = "Genetics & Evolution",
                title = "Lecture 01: Mendel's Laws of Inheritance & Monohybrid Cross",
                durationMinutes = 55,
                teacherName = "Dr. Yasmin Siddiqui",
                watchProgressPercent = 0.0f,
                isCompleted = false,
                viewsCount = 510
            )
        )
        dao.insertRecordedLectures(recorded)

        // 5. Study Materials
        val materials = listOf(
            StudyMaterialEntity(
                id = "mat_1",
                courseId = "c_jee",
                subject = "Physics",
                chapterName = "Rotational Motion",
                title = "Complete Formula Handbook & Derivations",
                fileType = "PDF",
                fileSize = "4.2 MB",
                isBookmarked = true,
                isDownloaded = true,
                uploadDate = "2 days ago",
                contentPreview = "Includes Parallel & Perpendicular Axes theorems, Radius of Gyration tables, Rolling without slipping equations and JEE Advanced shortcuts."
            ),
            StudyMaterialEntity(
                id = "mat_2",
                courseId = "c_jee",
                subject = "Mathematics",
                chapterName = "Calculus",
                title = "Integral Calculus 100 Solved Problems with Tricks",
                fileType = "PDF",
                fileSize = "6.8 MB",
                isBookmarked = true,
                isDownloaded = false,
                uploadDate = "Yesterday",
                contentPreview = "Special forms of rational integrals, Euler substitutions, Wallis formula and previous 10 years JEE Mains & Advanced questions."
            ),
            StudyMaterialEntity(
                id = "mat_3",
                courseId = "c_jee",
                subject = "Chemistry",
                chapterName = "Electrochemistry",
                title = "Handwritten Class Notes by Dr. Varma",
                fileType = "PDF",
                fileSize = "8.1 MB",
                isBookmarked = false,
                isDownloaded = false,
                uploadDate = "3 days ago",
                contentPreview = "Color-coded diagrams of Galvanic cells, Standard Hydrogen Electrode (SHE), Kohlrausch's law and fuel cells."
            ),
            StudyMaterialEntity(
                id = "mat_4",
                courseId = "c_neet",
                subject = "Biology",
                chapterName = "Genetics",
                title = "NCERT Line-by-Line High-Yield Cheat Sheet",
                fileType = "PDF",
                fileSize = "3.5 MB",
                isBookmarked = false,
                isDownloaded = false,
                uploadDate = "5 days ago",
                contentPreview = "Summary tables for chromosomal theory of inheritance, sex linkage in Drosophila, and pedigree analysis chart."
            )
        )
        dao.insertStudyMaterials(materials)

        // 6. Test & Exams
        val tests = listOf(
            TestExamEntity(
                id = "t_jee_1",
                courseId = "c_jee",
                subject = "Physics & Maths",
                title = "KGN All India Mock Test - Part 01",
                totalQuestions = 5,
                durationMinutes = 15,
                totalMarks = 20,
                negativeMarking = 1.0f,
                isPublished = true,
                attempted = false,
                score = 0,
                maxScore = 20
            ),
            TestExamEntity(
                id = "t_chem_1",
                courseId = "c_jee",
                subject = "Chemistry",
                title = "Chemical Kinetics & Solutions Chapter Test",
                totalQuestions = 4,
                durationMinutes = 10,
                totalMarks = 16,
                negativeMarking = 1.0f,
                isPublished = true,
                attempted = true,
                score = 12,
                maxScore = 16
            )
        )
        dao.insertTests(tests)

        // Questions for t_jee_1
        val questions = listOf(
            QuestionEntity(
                id = "q_1",
                testId = "t_jee_1",
                questionNumber = 1,
                questionText = "The moment of inertia of a uniform circular disc of mass M and radius R about an axis perpendicular to its plane and passing through its center is:",
                optionA = "MR² / 4",
                optionB = "MR² / 2",
                optionC = "MR²",
                optionD = "2MR² / 5",
                correctOption = 1,
                explanation = "For a uniform circular disc, integrating dm · r² from 0 to R yields I = (1/2)MR² about the central perpendicular axis."
            ),
            QuestionEntity(
                id = "q_2",
                testId = "t_jee_1",
                questionNumber = 2,
                questionText = "If the angular momentum of a rotating body is doubled while its moment of inertia remains constant, its rotational kinetic energy will:",
                optionA = "Remain unchanged",
                optionB = "Double",
                optionC = "Quadruple",
                optionD = "Become eightfold",
                correctOption = 2,
                explanation = "Rotational Kinetic Energy KE = L² / (2I). When angular momentum L doubles, KE becomes (2L)² / (2I) = 4 * KE."
            ),
            QuestionEntity(
                id = "q_3",
                testId = "t_jee_1",
                questionNumber = 3,
                questionText = "Evaluate the definite integral: ∫ from 0 to π/2 of (sin x) / (sin x + cos x) dx :",
                optionA = "π / 2",
                optionB = "π / 4",
                optionC = "π",
                optionD = "0",
                correctOption = 1,
                explanation = "Applying property ∫₀ᵃ f(x)dx = ∫₀ᵃ f(a-x)dx and adding the two forms gives 2I = ∫₀^(π/2) 1 dx = π/2 => I = π/4."
            ),
            QuestionEntity(
                id = "q_4",
                testId = "t_jee_1",
                questionNumber = 4,
                questionText = "A particle executes Simple Harmonic Motion with amplitude A. At what displacement from the mean position is its kinetic energy equal to its potential energy?",
                optionA = "A / 2",
                optionB = "A / √2",
                optionC = "A / 4",
                optionD = "A · √3 / 2",
                correctOption = 1,
                explanation = "KE = (1/2)k(A² - x²) and PE = (1/2)kx². Setting KE = PE gives A² - x² = x² => 2x² = A² => x = A / √2."
            ),
            QuestionEntity(
                id = "q_5",
                testId = "t_jee_1",
                questionNumber = 5,
                questionText = "The dimension of magnetic flux is:",
                optionA = "[M L² T⁻² A⁻¹]",
                optionB = "[M L T⁻² A⁻¹]",
                optionC = "[M L² T⁻¹ A⁻²]",
                optionD = "[M L⁰ T⁻² A⁻¹]",
                correctOption = 0,
                explanation = "Magnetic flux Φ = B · A = (F / (q·v)) · A. Dimension of F is MLT⁻², q is AT, v is LT⁻¹, Area is L². Thus [Φ] = [M L² T⁻² A⁻¹]."
            )
        )
        dao.insertQuestions(questions)

        // 7. Assignments
        val assignments = listOf(
            AssignmentEntity(
                id = "asn_1",
                courseId = "c_jee",
                subject = "Physics",
                title = "Rotational Mechanics Daily Practice Problem (DPP 08)",
                description = "Solve problems 1 to 15 from the workbook. Show complete step-by-step free body diagrams and torque balance equations.",
                dueDate = "Tomorrow, 11:59 PM",
                maxMarks = 30,
                status = "PENDING"
            ),
            AssignmentEntity(
                id = "asn_2",
                courseId = "c_jee",
                subject = "Mathematics",
                title = "Calculus Integration by Substitution Drill",
                description = "Submit PDF with solutions to exercises 7.1 to 7.4. Neat handwriting required.",
                dueDate = "3 days left",
                maxMarks = 25,
                status = "SUBMITTED",
                studentSubmissionText = "Uploaded solutions: KGN_Aamir_Integration_Worksheet.pdf"
            ),
            AssignmentEntity(
                id = "asn_3",
                courseId = "c_jee",
                subject = "Chemistry",
                title = "Electrochemistry Numerical Problems Worksheet",
                description = "Calculate cell EMF and Gibbs free energy for given Daniell cells at 298K.",
                dueDate = "Completed",
                maxMarks = 20,
                status = "EVALUATED",
                studentSubmissionText = "Handwritten worksheet submitted on Monday.",
                marksAwarded = 19,
                teacherFeedback = "Excellent work on Nernst equation calculations! Keep it up."
            )
        )
        dao.insertAssignments(assignments)

        // 8. Doubts
        val doubts = listOf(
            DoubtEntity(
                id = "dbt_1",
                studentName = "Aamir Khan",
                studentId = "u_std_1",
                subject = "Physics",
                topic = "Rotational Motion",
                questionText = "Sir, how does the friction act on a cylinder rolling down an incline without slipping? Why doesn't static friction do any work?",
                status = "RESOLVED",
                teacherReply = "Great question Aamir! In pure rolling, the instantaneous point of contact is momentarily at rest relative to the incline (v = 0). Since instantaneous displacement of the point of application of static friction is zero, work done by static friction is zero. It merely provides the torque for angular acceleration.",
                repliedAt = "Yesterday at 07:15 PM",
                createdAt = "Yesterday"
            ),
            DoubtEntity(
                id = "dbt_2",
                studentName = "Fatima Shaikh",
                studentId = "u_std_2",
                subject = "Chemistry",
                topic = "Thermodynamics",
                questionText = "Why is entropy change of the universe always positive for an irreversible spontaneous process?",
                status = "ANSWERED",
                teacherReply = "According to the Second Law of Thermodynamics, any spontaneous real process creates disorder. While system entropy can decrease locally, heat released to surroundings increases ΔS_surr such that ΔS_total > 0.",
                repliedAt = "Today at 02:30 PM",
                createdAt = "Today"
            ),
            DoubtEntity(
                id = "dbt_3",
                studentName = "Rohan Sharma",
                studentId = "u_std_3",
                subject = "Mathematics",
                topic = "Limits & Continuity",
                questionText = "How to handle 1^(infinity) indeterminate forms using exponential trick?",
                status = "UNDER_REVIEW",
                createdAt = "1 hour ago"
            )
        )
        for (d in doubts) dao.insertDoubt(d)

        // 9. Attendance
        val attendance = listOf(
            AttendanceRecordEntity("att_1", "u_std_1", "Aamir Khan", "28 Sep", "Physics Live Class", "PRESENT"),
            AttendanceRecordEntity("att_2", "u_std_1", "Aamir Khan", "27 Sep", "Mathematics Live Class", "PRESENT"),
            AttendanceRecordEntity("att_3", "u_std_1", "Aamir Khan", "26 Sep", "Chemistry Live Class", "PRESENT"),
            AttendanceRecordEntity("att_4", "u_std_1", "Aamir Khan", "25 Sep", "Physics Live Class", "PRESENT"),
            AttendanceRecordEntity("att_5", "u_std_1", "Aamir Khan", "24 Sep", "Mathematics Live Class", "LATE"),
            AttendanceRecordEntity("att_6", "u_std_1", "Aamir Khan", "23 Sep", "Chemistry Live Class", "PRESENT")
        )
        dao.insertAttendanceRecords(attendance)

        // 10. Notifications
        val notifications = listOf(
            NotificationEntity(
                id = "notif_1",
                title = "🔴 Live Class Now Starting!",
                message = "Prof. Salman Qureshi is live now: 'Rotational Mechanics: Moment of Inertia & Torque'. Tap to join immediately.",
                type = "LIVE",
                timestamp = "Just Now",
                isRead = false
            ),
            NotificationEntity(
                id = "notif_2",
                title = "📝 Test Result Published",
                message = "Your result for Chemistry Chapter Test is out. You scored 12/16 (75%). Check full explanation and rank now.",
                type = "EXAM",
                timestamp = "2 hours ago",
                isRead = false
            ),
            NotificationEntity(
                id = "notif_3",
                title = "📚 New Study Material Added",
                message = "Handwritten Class Notes on Integral Calculus (PDF) uploaded by Er. Ansari.",
                type = "ANNOUNCEMENT",
                timestamp = "Yesterday",
                isRead = true
            ),
            NotificationEntity(
                id = "notif_4",
                title = "🎉 Congratulations!",
                message = "You have maintained a 12-day study streak at NEW KGN INSTITUTE. Keep up the high dedication!",
                type = "ANNOUNCEMENT",
                timestamp = "2 days ago",
                isRead = true
            )
        )
        dao.insertNotifications(notifications)

        // 11. Payments
        val payment = PaymentEntity(
            id = "pay_101",
            studentId = "u_std_1",
            studentName = "Aamir Khan",
            courseTitle = "IIT-JEE 2026 Achiever Batch (PCM)",
            amount = 4999.0,
            transactionId = "TXN_KGN_98472918",
            date = "15 Sep 2026",
            status = "SUCCESS",
            invoiceNumber = "INV-2026-KGN-0082"
        )
        dao.insertPayment(payment)
    }

    suspend fun authenticateUser(email: String, pass: String): UserEntity? {
        val user = dao.getUserByEmail(email.trim()) ?: return null
        return if (user.passwordHash == pass) user else null
    }

    suspend fun registerUser(user: UserEntity) {
        dao.insertUser(user)
    }

    suspend fun updateUser(user: UserEntity) {
        dao.updateUser(user)
    }

    suspend fun enrollInCourse(courseId: String, student: UserEntity, amount: Double) {
        dao.enrollInCourse(courseId)
        val course = dao.getCourseById(courseId)
        val payment = PaymentEntity(
            id = "pay_" + System.currentTimeMillis(),
            studentId = student.id,
            studentName = student.name,
            courseTitle = course?.title ?: "KGN Course",
            amount = amount,
            transactionId = "TXN_KGN_" + (100000..999999).random(),
            date = "Today",
            status = "SUCCESS",
            invoiceNumber = "INV-KGN-" + (1000..9999).random()
        )
        dao.insertPayment(payment)
    }

    suspend fun createLiveClass(liveClass: LiveClassEntity) = dao.insertLiveClass(liveClass)
    suspend fun updateLiveClassStatus(classId: String, status: String) = dao.updateLiveClassStatus(classId, status)
    suspend fun toggleReminder(classId: String, reminder: Boolean) = dao.toggleReminder(classId, reminder)

    suspend fun updateLectureProgress(lectureId: String, progress: Float, completed: Boolean) =
        dao.updateLectureProgress(lectureId, progress, completed)

    suspend fun toggleBookmark(materialId: String, bookmarked: Boolean) = dao.toggleBookmark(materialId, bookmarked)
    suspend fun setDownloaded(materialId: String, downloaded: Boolean) = dao.setDownloaded(materialId, downloaded)
    suspend fun addStudyMaterial(material: StudyMaterialEntity) = dao.insertStudyMaterial(material)

    suspend fun getQuestionsForTest(testId: String): List<QuestionEntity> = dao.getQuestionsForTest(testId)
    suspend fun recordTestResult(testId: String, score: Int) = dao.recordTestResult(testId, score)

    suspend fun submitAssignment(assignmentId: String, submissionText: String) =
        dao.submitAssignment(assignmentId, submissionText)
    suspend fun gradeAssignment(assignmentId: String, marks: Int, feedback: String) =
        dao.gradeAssignment(assignmentId, marks, feedback)
    suspend fun createAssignment(assignment: AssignmentEntity) = dao.insertAssignment(assignment)

    suspend fun askDoubt(doubt: DoubtEntity) = dao.insertDoubt(doubt)
    suspend fun answerDoubt(doubtId: String, reply: String, time: String) = dao.answerDoubt(doubtId, reply, time)

    suspend fun markAllNotificationsAsRead() = dao.markAllNotificationsAsRead()
    suspend fun addNotification(notification: NotificationEntity) = dao.insertNotification(notification)

    suspend fun awardPoints(userId: String, points: Int) = dao.addPointsToUser(userId, points)
    suspend fun incrementStreak(userId: String) = dao.incrementStreak(userId)

    suspend fun recordReferral(referral: ReferralRecordEntity) = dao.insertReferral(referral)

    suspend fun deleteUser(userId: String) = dao.deleteUser(userId)
    suspend fun deleteCourse(courseId: String) = dao.deleteCourse(courseId)
    suspend fun createCourse(course: CourseEntity) = dao.insertCourse(course)
}
