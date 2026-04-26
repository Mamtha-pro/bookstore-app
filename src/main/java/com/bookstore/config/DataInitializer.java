package com.bookstore.config;

import com.bookstore.constants.Role;
import com.bookstore.entity.Book;
import com.bookstore.entity.User;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        createAdminUser();
        createSampleBooks();
    }

    // ── Create Admin User ─────────────────────────────────────────────
    private void createAdminUser() {
        if (!userRepository.existsByEmail("admin@bookstore.com")) {
            User admin = User.builder()
                    .name("Admin")
                    .email("admin@bookstore.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(admin);
            log.info("✅ Admin created → admin@bookstore.com / admin123");
        }

        if (!userRepository.existsByEmail("user@bookstore.com")) {
            User user = User.builder()
                    .name("Test User")
                    .email("user@bookstore.com")
                    .password(passwordEncoder.encode("user123"))
                    .role(Role.USER)
                    .build();
            userRepository.save(user);
            log.info("✅ Test user created → user@bookstore.com / user123");
        }
    }

    private void createSampleBooks() {
        if (bookRepository.count() > 0) {
            log.info("📚 Books already exist — skipping");
            return;
        }

        List<Book> books = List.of(

                // ══════════════════════════════════════════
                // 📗 PROGRAMMING (20 books)
                // ══════════════════════════════════════════
                Book.builder().title("Clean Code")
                        .author("Robert C. Martin").isbn("9780132350884")
                        .price(499.00).category("Programming").stock(50)
                        .description("A handbook of agile software craftsmanship. Learn to write clean, readable, maintainable code.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780132350884-L.jpg").build(),

                Book.builder().title("The Pragmatic Programmer")
                        .author("Andrew Hunt").isbn("9780135957059")
                        .price(599.00).category("Programming").stock(35)
                        .description("Your journey to mastery — practical tips for programmers at every level.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780135957059-L.jpg").build(),

                Book.builder().title("Design Patterns")
                        .author("Gang of Four").isbn("9780201633610")
                        .price(749.00).category("Programming").stock(20)
                        .description("Classic book on reusable object-oriented software design patterns.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780201633610-L.jpg").build(),

                Book.builder().title("Introduction to Algorithms")
                        .author("Thomas H. Cormen").isbn("9780262033848")
                        .price(899.00).category("Programming").stock(25)
                        .description("The most comprehensive guide to algorithms — sorting, graphs, dynamic programming and more.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780262033848-L.jpg").build(),

                Book.builder().title("Spring Boot in Action")
                        .author("Craig Walls").isbn("9781617292545")
                        .price(449.00).category("Programming").stock(40)
                        .description("Build production-ready Spring Boot applications quickly and efficiently.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9781617292545-L.jpg").build(),

                Book.builder().title("Head First Java")
                        .author("Kathy Sierra").isbn("9780596009205")
                        .price(399.00).category("Programming").stock(60)
                        .description("A brain-friendly guide to Java — the most beginner-friendly Java book ever written.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780596009205-L.jpg").build(),

                Book.builder().title("Effective Java")
                        .author("Joshua Bloch").isbn("9780134685991")
                        .price(649.00).category("Programming").stock(30)
                        .description("Best practices for the Java platform — 90 items every Java developer must know.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780134685991-L.jpg").build(),

                Book.builder().title("You Don't Know JS")
                        .author("Kyle Simpson").isbn("9781491924464")
                        .price(349.00).category("Programming").stock(45)
                        .description("Deep dive into JavaScript's core mechanisms — scopes, closures, prototypes.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9781491924464-L.jpg").build(),

                Book.builder().title("Python Crash Course")
                        .author("Eric Matthes").isbn("9781593279288")
                        .price(429.00).category("Programming").stock(55)
                        .description("A hands-on, project-based introduction to Python programming for beginners.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9781593279288-L.jpg").build(),

                Book.builder().title("JavaScript: The Good Parts")
                        .author("Douglas Crockford").isbn("9780596517748")
                        .price(299.00).category("Programming").stock(40)
                        .description("Unearthing the excellence in JavaScript — a must-read for every JS developer.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780596517748-L.jpg").build(),

                Book.builder().title("The Art of Computer Programming")
                        .author("Donald E. Knuth").isbn("9780201896831")
                        .price(1299.00).category("Programming").stock(15)
                        .description("The definitive multi-volume work on algorithms — the bible of computer science.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780201896831-L.jpg").build(),

                Book.builder().title("Refactoring")
                        .author("Martin Fowler").isbn("9780134757599")
                        .price(699.00).category("Programming").stock(28)
                        .description("Improving the design of existing code — with clear catalog of refactoring techniques.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780134757599-L.jpg").build(),

                Book.builder().title("Structure and Interpretation of Computer Programs")
                        .author("Harold Abelson").isbn("9780262510875")
                        .price(799.00).category("Programming").stock(18)
                        .description("The legendary MIT textbook on programming — teaches deep thinking about computation.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780262510875-L.jpg").build(),

                Book.builder().title("Docker Deep Dive")
                        .author("Nigel Poulton").isbn("9781521822807")
                        .price(349.00).category("Programming").stock(50)
                        .description("Master Docker containers from zero to production — clear, concise, practical.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9781521822807-L.jpg").build(),

                Book.builder().title("Kubernetes in Action")
                        .author("Marko Luksa").isbn("9781617293726")
                        .price(799.00).category("Programming").stock(22)
                        .description("Full guide to deploying, managing and scaling applications with Kubernetes.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9781617293726-L.jpg").build(),

                Book.builder().title("Clean Architecture")
                        .author("Robert C. Martin").isbn("9780134494166")
                        .price(599.00).category("Programming").stock(33)
                        .description("A craftsman's guide to software structure and design — timeless principles.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780134494166-L.jpg").build(),

                Book.builder().title("Microservices Patterns")
                        .author("Chris Richardson").isbn("9781617294549")
                        .price(699.00).category("Programming").stock(25)
                        .description("How to build production-grade microservices — with real-world patterns and examples.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9781617294549-L.jpg").build(),

                Book.builder().title("React: Up and Running")
                        .author("Stoyan Stefanov").isbn("9781492051466")
                        .price(449.00).category("Programming").stock(38)
                        .description("Build modern web applications using React — fast, practical, beginner-friendly.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9781492051466-L.jpg").build(),

                Book.builder().title("Database Internals")
                        .author("Alex Petrov").isbn("9781492040347")
                        .price(849.00).category("Programming").stock(20)
                        .description("A deep dive into how distributed databases and storage engines work internally.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9781492040347-L.jpg").build(),

                Book.builder().title("The Linux Command Line")
                        .author("William Shotts").isbn("9781593279523")
                        .price(379.00).category("Programming").stock(45)
                        .description("A complete introduction to Linux — command line tools, shell scripting and more.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9781593279523-L.jpg").build(),

                // ══════════════════════════════════════════
                // 📘 FICTION (20 books)
                // ══════════════════════════════════════════
                Book.builder().title("The Alchemist")
                        .author("Paulo Coelho").isbn("9780062315007")
                        .price(299.00).category("Fiction").stock(100)
                        .description("A magical story about following your dreams — one of the best-selling books of all time.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780062315007-L.jpg").build(),

                Book.builder().title("Harry Potter and the Sorcerer's Stone")
                        .author("J.K. Rowling").isbn("9780439708180")
                        .price(349.00).category("Fiction").stock(80)
                        .description("The beginning of Harry Potter's magical journey at Hogwarts School of Witchcraft.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780439708180-L.jpg").build(),

                Book.builder().title("To Kill a Mockingbird")
                        .author("Harper Lee").isbn("9780061935466")
                        .price(249.00).category("Fiction").stock(60)
                        .description("A powerful story of racial injustice and moral growth. Winner of the Pulitzer Prize.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780061935466-L.jpg").build(),

                Book.builder().title("1984")
                        .author("George Orwell").isbn("9780451524935")
                        .price(279.00).category("Fiction").stock(55)
                        .description("A dystopian masterpiece about totalitarianism and surveillance. More relevant than ever.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780451524935-L.jpg").build(),

                Book.builder().title("The Great Gatsby")
                        .author("F. Scott Fitzgerald").isbn("9780743273565")
                        .price(199.00).category("Fiction").stock(45)
                        .description("A classic American novel about wealth, love, and the American Dream in the roaring 1920s.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780743273565-L.jpg").build(),

                Book.builder().title("Pride and Prejudice")
                        .author("Jane Austen").isbn("9780141439518")
                        .price(179.00).category("Fiction").stock(70)
                        .description("Jane Austen's beloved romantic novel about love, class and marriage in 19th century England.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780141439518-L.jpg").build(),

                Book.builder().title("The Catcher in the Rye")
                        .author("J.D. Salinger").isbn("9780316769174")
                        .price(229.00).category("Fiction").stock(50)
                        .description("Holden Caulfield's iconic journey through New York — a defining novel of teenage rebellion.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780316769174-L.jpg").build(),

                Book.builder().title("Brave New World")
                        .author("Aldous Huxley").isbn("9780060850524")
                        .price(259.00).category("Fiction").stock(42)
                        .description("A terrifying vision of a future society controlled by pleasure and technology.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780060850524-L.jpg").build(),

                Book.builder().title("The Lord of the Rings")
                        .author("J.R.R. Tolkien").isbn("9780618640157")
                        .price(699.00).category("Fiction").stock(35)
                        .description("The greatest fantasy epic ever written — the complete trilogy in one volume.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780618640157-L.jpg").build(),

                Book.builder().title("The Hitchhiker's Guide to the Galaxy")
                        .author("Douglas Adams").isbn("9780345391803")
                        .price(249.00).category("Fiction").stock(55)
                        .description("The funniest science fiction ever written — 42 is the answer to everything.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780345391803-L.jpg").build(),

                Book.builder().title("Dune")
                        .author("Frank Herbert").isbn("9780441013593")
                        .price(449.00).category("Fiction").stock(40)
                        .description("The greatest science fiction novel ever written — a sweeping interplanetary epic.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780441013593-L.jpg").build(),

                Book.builder().title("The Da Vinci Code")
                        .author("Dan Brown").isbn("9780307474278")
                        .price(329.00).category("Fiction").stock(65)
                        .description("A breathtaking thriller involving a murder in the Louvre and a 2000-year old conspiracy.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780307474278-L.jpg").build(),

                Book.builder().title("Gone Girl")
                        .author("Gillian Flynn").isbn("9780307588364")
                        .price(349.00).category("Fiction").stock(48)
                        .description("A dark psychological thriller about a marriage gone terrifyingly wrong.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780307588364-L.jpg").build(),

                Book.builder().title("The Kite Runner")
                        .author("Khaled Hosseini").isbn("9781594631931")
                        .price(299.00).category("Fiction").stock(52)
                        .description("A moving story of friendship, betrayal and redemption set against Afghanistan's history.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9781594631931-L.jpg").build(),

                Book.builder().title("Life of Pi")
                        .author("Yann Martel").isbn("9780156027328")
                        .price(279.00).category("Fiction").stock(44)
                        .description("A boy survives 227 days at sea with a Bengal tiger — a profound story of faith and survival.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780156027328-L.jpg").build(),

                Book.builder().title("Atomic")
                        .author("Jim Baggott").isbn("9780199691906")
                        .price(399.00).category("Fiction").stock(30)
                        .description("The first war of physics and the secret history of the atom bomb.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780199691906-L.jpg").build(),

                Book.builder().title("The Hunger Games")
                        .author("Suzanne Collins").isbn("9780439023481")
                        .price(319.00).category("Fiction").stock(70)
                        .description("In a dystopian future, teenagers are forced to fight to the death in televised games.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780439023481-L.jpg").build(),

                Book.builder().title("Sherlock Holmes: Complete Stories")
                        .author("Arthur Conan Doyle").isbn("9781840228137")
                        .price(499.00).category("Fiction").stock(38)
                        .description("All 60 Sherlock Holmes stories in one complete volume — the world's greatest detective.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9781840228137-L.jpg").build(),

                Book.builder().title("And Then There Were None")
                        .author("Agatha Christie").isbn("9780062073488")
                        .price(219.00).category("Fiction").stock(60)
                        .description("Ten strangers stranded on an island — one by one they are murdered. Who is the killer?")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780062073488-L.jpg").build(),

                Book.builder().title("The Girl with the Dragon Tattoo")
                        .author("Stieg Larsson").isbn("9780307454546")
                        .price(369.00).category("Fiction").stock(43)
                        .description("A gripping thriller about a journalist and hacker investigating a 40-year-old disappearance.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780307454546-L.jpg").build(),

                // ══════════════════════════════════════════
                // 📙 SELF HELP (15 books)
                // ══════════════════════════════════════════
                Book.builder().title("Atomic Habits")
                        .author("James Clear").isbn("9780735211292")
                        .price(399.00).category("Self Help").stock(75)
                        .description("Tiny changes, remarkable results — the proven system to build good habits.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780735211292-L.jpg").build(),

                Book.builder().title("Think and Grow Rich")
                        .author("Napoleon Hill").isbn("9781585424337")
                        .price(249.00).category("Self Help").stock(90)
                        .description("The classic book that has helped millions achieve success through the power of thoughts.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9781585424337-L.jpg").build(),

                Book.builder().title("The 7 Habits of Highly Effective People")
                        .author("Stephen R. Covey").isbn("9781982137274")
                        .price(449.00).category("Self Help").stock(65)
                        .description("Powerful lessons in personal change — one of the most influential business books ever.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9781982137274-L.jpg").build(),

                Book.builder().title("Rich Dad Poor Dad")
                        .author("Robert T. Kiyosaki").isbn("9781612680194")
                        .price(329.00).category("Self Help").stock(85)
                        .description("What the rich teach their kids about money that the poor and middle class do not.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9781612680194-L.jpg").build(),

                Book.builder().title("The Power of Now")
                        .author("Eckhart Tolle").isbn("9781577314806")
                        .price(299.00).category("Self Help").stock(60)
                        .description("A guide to spiritual enlightenment — live in the present moment and find inner peace.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9781577314806-L.jpg").build(),

                Book.builder().title("How to Win Friends and Influence People")
                        .author("Dale Carnegie").isbn("9780671027032")
                        .price(279.00).category("Self Help").stock(80)
                        .description("The ultimate guide to people skills — timeless advice that still works perfectly today.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780671027032-L.jpg").build(),

                Book.builder().title("The Subtle Art of Not Giving a F*ck")
                        .author("Mark Manson").isbn("9780062457714")
                        .price(349.00).category("Self Help").stock(70)
                        .description("A counterintuitive approach to living a good life — focus on what truly matters.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780062457714-L.jpg").build(),

                Book.builder().title("Mindset: The New Psychology of Success")
                        .author("Carol S. Dweck").isbn("9780345472328")
                        .price(319.00).category("Self Help").stock(55)
                        .description("How a growth mindset can help you fulfil your potential in every area of life.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780345472328-L.jpg").build(),

                Book.builder().title("Deep Work")
                        .author("Cal Newport").isbn("9781455586691")
                        .price(369.00).category("Self Help").stock(50)
                        .description("Rules for focused success in a distracted world — do more in less time.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9781455586691-L.jpg").build(),

                Book.builder().title("The Miracle Morning")
                        .author("Hal Elrod").isbn("9780979019777")
                        .price(259.00).category("Self Help").stock(62)
                        .description("The not-so-obvious secret guaranteed to transform your life — before 8AM.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780979019777-L.jpg").build(),

                Book.builder().title("Can't Hurt Me")
                        .author("David Goggins").isbn("9781544512280")
                        .price(429.00).category("Self Help").stock(48)
                        .description("Master your mind and defy the odds — the most inspiring memoir of mental toughness.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9781544512280-L.jpg").build(),

                Book.builder().title("The 4-Hour Work Week")
                        .author("Timothy Ferriss").isbn("9780307465351")
                        .price(349.00).category("Self Help").stock(45)
                        .description("Escape the 9-5, live anywhere and join the new rich — a blueprint for lifestyle design.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780307465351-L.jpg").build(),

                Book.builder().title("Man's Search for Meaning")
                        .author("Viktor E. Frankl").isbn("9780807014271")
                        .price(219.00).category("Self Help").stock(72)
                        .description("A Holocaust survivor's account of finding purpose in suffering — one of the greatest books.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780807014271-L.jpg").build(),

                Book.builder().title("Thinking, Fast and Slow")
                        .author("Daniel Kahneman").isbn("9780374533557")
                        .price(499.00).category("Self Help").stock(40)
                        .description("How two systems drive the way we think — a groundbreaking work on decision making.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780374533557-L.jpg").build(),

                Book.builder().title("The Compound Effect")
                        .author("Darren Hardy").isbn("9781593157630")
                        .price(279.00).category("Self Help").stock(58)
                        .description("Multiplying your success one simple step at a time — small actions lead to big results.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9781593157630-L.jpg").build(),

                // ══════════════════════════════════════════
                // 🔬 SCIENCE (15 books)
                // ══════════════════════════════════════════
                Book.builder().title("A Brief History of Time")
                        .author("Stephen Hawking").isbn("9780553380163")
                        .price(349.00).category("Science").stock(40)
                        .description("Cosmology, black holes, and the nature of time — science made wonderfully accessible.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780553380163-L.jpg").build(),

                Book.builder().title("Sapiens")
                        .author("Yuval Noah Harari").isbn("9780062316097")
                        .price(499.00).category("Science").stock(50)
                        .description("A brief history of humankind — how Homo sapiens came to dominate the planet.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780062316097-L.jpg").build(),

                Book.builder().title("The Selfish Gene")
                        .author("Richard Dawkins").isbn("9780198788607")
                        .price(379.00).category("Science").stock(32)
                        .description("A revolutionary view of evolution — genes are the true unit of natural selection.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780198788607-L.jpg").build(),

                Book.builder().title("Cosmos")
                        .author("Carl Sagan").isbn("9780345539434")
                        .price(449.00).category("Science").stock(36)
                        .description("A personal voyage through the universe — Carl Sagan's masterpiece on science and humanity.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780345539434-L.jpg").build(),

                Book.builder().title("The Gene")
                        .author("Siddhartha Mukherjee").isbn("9781476733500")
                        .price(549.00).category("Science").stock(28)
                        .description("An intimate history of genetics from Mendel's peas to CRISPR gene editing.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9781476733500-L.jpg").build(),

                Book.builder().title("Astrophysics for People in a Hurry")
                        .author("Neil deGrasse Tyson").isbn("9780393609394")
                        .price(299.00).category("Science").stock(55)
                        .description("The essential universe — from the Big Bang to dark energy — in bite-size chapters.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780393609394-L.jpg").build(),

                Book.builder().title("The Origin of Species")
                        .author("Charles Darwin").isbn("9780140432053")
                        .price(199.00).category("Science").stock(30)
                        .description("Darwin's groundbreaking work that changed humanity's understanding of life on Earth.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780140432053-L.jpg").build(),

                Book.builder().title("Surely You're Joking, Mr. Feynman!")
                        .author("Richard Feynman").isbn("9780393316049")
                        .price(329.00).category("Science").stock(42)
                        .description("Adventures of a curious character — the hilarious and brilliant memoir of a Nobel laureate.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780393316049-L.jpg").build(),

                Book.builder().title("The Immortal Life of Henrietta Lacks")
                        .author("Rebecca Skloot").isbn("9781400052189")
                        .price(369.00).category("Science").stock(25)
                        .description("How one woman's cells changed medicine forever — a true story of science and ethics.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9781400052189-L.jpg").build(),

                Book.builder().title("Homo Deus")
                        .author("Yuval Noah Harari").isbn("9780062464316")
                        .price(499.00).category("Science").stock(38)
                        .description("A brief history of tomorrow — what happens when humans acquire godlike powers?")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780062464316-L.jpg").build(),

                Book.builder().title("The Body: A Guide for Occupants")
                        .author("Bill Bryson").isbn("9780767908184")
                        .price(449.00).category("Science").stock(33)
                        .description("A joyful tour of the human body — how it works and the remarkable science behind it.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780767908184-L.jpg").build(),

                Book.builder().title("Quantum Theory Cannot Hurt You")
                        .author("Marcus Chown").isbn("9780571225583")
                        .price(279.00).category("Science").stock(28)
                        .description("A painless guide to quantum theory and relativity — physics made genuinely fun.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780571225583-L.jpg").build(),

                Book.builder().title("The Double Helix")
                        .author("James D. Watson").isbn("9780743216302")
                        .price(319.00).category("Science").stock(22)
                        .description("The personal account of the discovery of DNA — one of science's greatest stories.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780743216302-L.jpg").build(),

                Book.builder().title("Inferior")
                        .author("Angela Saini").isbn("9780807071045")
                        .price(349.00).category("Science").stock(26)
                        .description("How science got women wrong and the new research that's rewriting the story.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780807071045-L.jpg").build(),

                Book.builder().title("The Sixth Extinction")
                        .author("Elizabeth Kolbert").isbn("9780805092998")
                        .price(399.00).category("Science").stock(30)
                        .description("An unnatural history — how human activity is causing the planet's sixth mass extinction.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780805092998-L.jpg").build(),

                // ══════════════════════════════════════════
                // 💼 BUSINESS (15 books)
                // ══════════════════════════════════════════
                Book.builder().title("Zero to One")
                        .author("Peter Thiel").isbn("9780804139021")
                        .price(399.00).category("Business").stock(45)
                        .description("Notes on startups — Peter Thiel's contrarian thinking on building great companies.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780804139021-L.jpg").build(),

                Book.builder().title("The Lean Startup")
                        .author("Eric Ries").isbn("9780307887894")
                        .price(449.00).category("Business").stock(55)
                        .description("How today's entrepreneurs use continuous innovation to create radical new products.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780307887894-L.jpg").build(),

                Book.builder().title("Good to Great")
                        .author("Jim Collins").isbn("9780066620992")
                        .price(499.00).category("Business").stock(35)
                        .description("Why some companies make the leap to greatness and others don't — based on 5 years research.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780066620992-L.jpg").build(),

                Book.builder().title("The Innovator's Dilemma")
                        .author("Clayton Christensen").isbn("9780062060242")
                        .price(549.00).category("Business").stock(28)
                        .description("Why new technologies cause great firms to fail — the most influential business book ever.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780062060242-L.jpg").build(),

                Book.builder().title("Start With Why")
                        .author("Simon Sinek").isbn("9781591846444")
                        .price(349.00).category("Business").stock(60)
                        .description("How great leaders inspire everyone to take action — find your WHY.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9781591846444-L.jpg").build(),

                Book.builder().title("The Hard Thing About Hard Things")
                        .author("Ben Horowitz").isbn("9780062273208")
                        .price(449.00).category("Business").stock(38)
                        .description("Building a business when there are no easy answers — raw, honest startup wisdom.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780062273208-L.jpg").build(),

                Book.builder().title("Built to Last")
                        .author("Jim Collins").isbn("9780060516405")
                        .price(499.00).category("Business").stock(30)
                        .description("Successful habits of visionary companies — what makes great companies endure for decades.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780060516405-L.jpg").build(),

                Book.builder().title("Crossing the Chasm")
                        .author("Geoffrey A. Moore").isbn("9780062292988")
                        .price(449.00).category("Business").stock(25)
                        .description("Marketing and selling disruptive products to mainstream customers — the startup bible.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780062292988-L.jpg").build(),

                Book.builder().title("The E-Myth Revisited")
                        .author("Michael E. Gerber").isbn("9780887307287")
                        .price(349.00).category("Business").stock(42)
                        .description("Why most small businesses don't work and what to do about it.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780887307287-L.jpg").build(),

                Book.builder().title("Purple Cow")
                        .author("Seth Godin").isbn("9781591843177")
                        .price(299.00).category("Business").stock(50)
                        .description("Transform your business by being remarkable — stand out or fade out.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9781591843177-L.jpg").build(),

                Book.builder().title("Shoe Dog")
                        .author("Phil Knight").isbn("9781501135927")
                        .price(399.00).category("Business").stock(45)
                        .description("Nike founder Phil Knight's memoir — the incredible story of building a global brand.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9781501135927-L.jpg").build(),

                Book.builder().title("Steve Jobs")
                        .author("Walter Isaacson").isbn("9781451648546")
                        .price(549.00).category("Business").stock(40)
                        .description("The exclusive biography of Apple's co-founder — based on 40 interviews with Jobs himself.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9781451648546-L.jpg").build(),

                Book.builder().title("Elon Musk")
                        .author("Walter Isaacson").isbn("9781982181284")
                        .price(599.00).category("Business").stock(50)
                        .description("The definitive biography of the world's most controversial entrepreneur.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9781982181284-L.jpg").build(),

                Book.builder().title("Measure What Matters")
                        .author("John Doerr").isbn("9780525536222")
                        .price(449.00).category("Business").stock(32)
                        .description("OKRs — the goal-setting system used by Google, Intel and Intel to achieve 10x growth.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780525536222-L.jpg").build(),

                Book.builder().title("Atomic Habits for Business")
                        .author("James Clear").isbn("9780593189641")
                        .price(379.00).category("Business").stock(38)
                        .description("Applying tiny habit changes to achieve massive business results and team transformation.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780593189641-L.jpg").build(),

                // ══════════════════════════════════════════
                // 🧘 SPIRITUALITY (10 books)
                // ══════════════════════════════════════════
                Book.builder().title("The Bhagavad Gita")
                        .author("Eknath Easwaran").isbn("9781586380199")
                        .price(199.00).category("Spirituality").stock(90)
                        .description("The timeless Indian scripture on duty, devotion and the path to liberation.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9781586380199-L.jpg").build(),

                Book.builder().title("Autobiography of a Yogi")
                        .author("Paramahansa Yogananda").isbn("9780876120835")
                        .price(299.00).category("Spirituality").stock(65)
                        .description("One of the most widely read spiritual classics — Steve Jobs read it every year.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780876120835-L.jpg").build(),

                Book.builder().title("The Book of Joy")
                        .author("Dalai Lama").isbn("9780399185045")
                        .price(349.00).category("Spirituality").stock(55)
                        .description("Lasting happiness in a changing world — conversations between the Dalai Lama and Desmond Tutu.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780399185045-L.jpg").build(),

                Book.builder().title("Siddhartha")
                        .author("Hermann Hesse").isbn("9780553208849")
                        .price(179.00).category("Spirituality").stock(70)
                        .description("A spiritual journey of self-discovery in ancient India — one of the great novels of wisdom.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780553208849-L.jpg").build(),

                Book.builder().title("The Prophet")
                        .author("Kahlil Gibran").isbn("9780394404288")
                        .price(149.00).category("Spirituality").stock(80)
                        .description("Profound wisdom on love, freedom, joy and sorrow — one of the best-selling books ever.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780394404288-L.jpg").build(),

                Book.builder().title("A New Earth")
                        .author("Eckhart Tolle").isbn("9780452289963")
                        .price(319.00).category("Spirituality").stock(50)
                        .description("Awakening to your life's purpose — a blueprint for personal transformation.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780452289963-L.jpg").build(),

                Book.builder().title("The Miracle of Mindfulness")
                        .author("Thich Nhat Hanh").isbn("9780807012390")
                        .price(199.00).category("Spirituality").stock(60)
                        .description("An introduction to meditation — gentle, practical guide to present-moment awareness.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780807012390-L.jpg").build(),

                Book.builder().title("Wings of Fire")
                        .author("A.P.J. Abdul Kalam").isbn("9788173711466")
                        .price(179.00).category("Spirituality").stock(100)
                        .description("The inspiring autobiography of India's Missile Man and beloved President.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9788173711466-L.jpg").build(),

                Book.builder().title("The Monk Who Sold His Ferrari")
                        .author("Robin Sharma").isbn("9780062515926")
                        .price(249.00).category("Spirituality").stock(75)
                        .description("A fable about fulfilling your dreams and reaching your destiny.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780062515926-L.jpg").build(),

                Book.builder().title("Inner Engineering")
                        .author("Sadhguru").isbn("9780812997798")
                        .price(299.00).category("Spirituality").stock(68)
                        .description("A yogi's guide to joy — Sadhguru's powerful toolkit for transforming your life within.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780812997798-L.jpg").build(),

                // ══════════════════════════════════════════
                // 🎨 DESIGN (5 books)
                // ══════════════════════════════════════════
                Book.builder().title("The Design of Everyday Things")
                        .author("Don Norman").isbn("9780465050659")
                        .price(499.00).category("Design").stock(30)
                        .description("Why some products satisfy customers while others only frustrate them — a design bible.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780465050659-L.jpg").build(),

                Book.builder().title("Don't Make Me Think")
                        .author("Steve Krug").isbn("9780321965516")
                        .price(449.00).category("Design").stock(35)
                        .description("A common sense approach to web usability — the most readable UX book ever written.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780321965516-L.jpg").build(),

                Book.builder().title("Hooked")
                        .author("Nir Eyal").isbn("9781591847786")
                        .price(369.00).category("Design").stock(40)
                        .description("How to build habit-forming products — the psychology behind the apps we can't stop using.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9781591847786-L.jpg").build(),

                Book.builder().title("Sprint")
                        .author("Jake Knapp").isbn("9781501121746")
                        .price(399.00).category("Design").stock(28)
                        .description("Solve big problems and test new ideas in just five days — Google Ventures design sprint.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9781501121746-L.jpg").build(),

                Book.builder().title("Creative Confidence")
                        .author("Tom Kelley").isbn("9780385349369")
                        .price(429.00).category("Design").stock(25)
                        .description("Unleashing the creative potential within us all — from IDEO's founders.")
                        .imageUrl("https://covers.openlibrary.org/b/isbn/9780385349369-L.jpg").build()
        );

        bookRepository.saveAll(books);
        log.info("✅ {} books loaded successfully! 🎉", books.size());
    }}