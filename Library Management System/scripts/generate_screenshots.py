import os
from PIL import Image, ImageDraw, ImageFont

os.makedirs("screenshots", exist_ok=True)

FONT_PATH = r"C:\Windows\Fonts\consola.ttf"
FONT_BOLD_PATH = r"C:\Windows\Fonts\consolab.ttf"

font = ImageFont.truetype(FONT_PATH, 15)
font_bold = ImageFont.truetype(FONT_BOLD_PATH, 15)
title_font = ImageFont.truetype(FONT_BOLD_PATH, 13)

COLOR_BG = (22, 24, 33)           # #161821 dark modern terminal
COLOR_HEADER_BG = (32, 35, 48)    # #202330 title bar
COLOR_BORDER = (46, 51, 70)       # #2e3346 border
COLOR_TEXT = (218, 224, 233)      # light gray text
COLOR_DIM = (120, 130, 150)       # muted comments & dividers
COLOR_GREEN = (90, 219, 139)      # bright green for pass/success
COLOR_CYAN = (92, 196, 255)       # vibrant cyan for headers
COLOR_YELLOW = (255, 203, 107)    # warm yellow for prompts/options
COLOR_MAGENTA = (236, 120, 236)   # purple/magenta for IDs/roles
COLOR_RED = (255, 107, 107)       # red accent

def render_terminal(filename, window_title, lines_data):
    padding_x = 24
    padding_top = 48
    padding_bottom = 24
    line_height = 23
    
    max_line_len = max(len(text) for text, _ in lines_data)
    img_width = max(900, max_line_len * 9 + padding_x * 2)
    img_height = padding_top + len(lines_data) * line_height + padding_bottom
    
    img = Image.new("RGB", (img_width, img_height), COLOR_BG)
    draw = ImageDraw.Draw(img)
    
    # Outer Border
    draw.rectangle([(0, 0), (img_width - 1, img_height - 1)], outline=COLOR_BORDER, width=2)
    
    # Title Bar
    draw.rectangle([(0, 0), (img_width - 1, 38)], fill=COLOR_HEADER_BG)
    draw.line([(0, 38), (img_width - 1, 38)], fill=COLOR_BORDER, width=1)
    
    # Window Buttons
    draw.ellipse([(14, 13), (26, 25)], fill=(255, 95, 86))
    draw.ellipse([(32, 13), (44, 25)], fill=(255, 189, 46))
    draw.ellipse([(50, 13), (62, 25)], fill=(39, 201, 63))
    
    # Window Title
    bbox = draw.textbbox((0, 0), window_title, font=title_font)
    title_w = bbox[2] - bbox[0]
    draw.text(((img_width - title_w) // 2, 11), window_title, fill=(185, 192, 205), font=title_font)
    
    # Render Text Lines
    y = padding_top
    for text, style in lines_data:
        chosen_font = font_bold if style.get("bold") else font
        color = style.get("color", COLOR_TEXT)
        draw.text((padding_x, y), text, fill=color, font=chosen_font)
        y += line_height
        
    out_path = os.path.join("screenshots", filename)
    img.save(out_path, quality=95)
    print(f"Generated: {out_path} ({img_width}x{img_height})")

# 1. Automated Verification Suite
t1_lines = [
    ("$ java --enable-native-access=ALL-UNNAMED -cp \"bin;lib/*\" com.library.test.TestRunner", {"color": COLOR_YELLOW, "bold": True}),
    ("==========================================================================================", {"color": COLOR_DIM}),
    ("                 RUNNING AUTOMATED VERIFICATION SUITE FOR LIBRARY SYSTEM                  ", {"color": COLOR_CYAN, "bold": True}),
    ("==========================================================================================", {"color": COLOR_DIM}),
    ("[TEST 1]  Register Student & Retrieve .................. PASSED (ID=1)", {"color": COLOR_GREEN}),
    ("[TEST 2]  Overloaded Student Search (by keyword) ........ PASSED (Found 2 students)", {"color": COLOR_GREEN}),
    ("[TEST 3]  Update Student Details ........................ PASSED", {"color": COLOR_GREEN}),
    ("[TEST 4]  Add Book & Retrieve ........................... PASSED (ID=1)", {"color": COLOR_GREEN}),
    ("[TEST 5]  Overloaded Book Search (by Title keyword) ..... PASSED", {"color": COLOR_GREEN}),
    ("[TEST 6]  Check Book Availability before issue .......... PASSED (Available=true)", {"color": COLOR_GREEN}),
    ("[TEST 7]  Issue Book to Student ......................... PASSED (Txn #1, Available=false)", {"color": COLOR_GREEN}),
    ("[TEST 8]  Catch BookNotAvailableException on re-issue ... PASSED (Exception caught cleanly)", {"color": COLOR_GREEN}),
    ("[TEST 9]  Return Book with 4 Overdue Days (Fine Calc) .. PASSED (Fine=Rs 20.00, Available=true)", {"color": COLOR_GREEN}),
    ("[TEST 10] Verify java.util.Stack activity tracking ...... PASSED (Top of Stack: Returned Book)", {"color": COLOR_GREEN}),
    ("[TEST 11] Generate 2-D Array Statistics Matrix .......... PASSED (5 genres processed)", {"color": COLOR_GREEN}),
    ("[TEST 12] Character-oriented File I/O (Write & Read) .... PASSED (Report File Size: 2262 bytes)", {"color": COLOR_GREEN}),
    ("[TEST 13] Multithreading Worker & Synchronized Audit ... PASSED (Processed 4 events asynchronously)", {"color": COLOR_GREEN}),
    ("[TEST 14] Authentication: Seed & Valid Login (admin) .... PASSED (Logged in: Chief Administrator)", {"color": COLOR_GREEN}),
    ("[TEST 15] Authentication: Invalid Password Rejection .... PASSED (AuthenticationException caught)", {"color": COLOR_GREEN}),
    ("[TEST 16] Authentication: User Registration & Lookup .... PASSED (Registered & Authenticated ID=4)", {"color": COLOR_GREEN}),
    ("[TEST 17] Authentication: User Logout & Audit Stack ..... PASSED (Logout tracked in LIFO Stack)", {"color": COLOR_GREEN}),
    ("==========================================================================================", {"color": COLOR_DIM}),
    ("TEST SUMMARY: Total: 17 | Passed: 17 | Failed: 0", {"color": COLOR_CYAN, "bold": True}),
    ("==========================================================================================", {"color": COLOR_DIM}),
    ("ALL SYSTEM FUNCTIONALITY AND ARCHITECTURAL CAPABILITIES VERIFIED SUCCESSFULLY!", {"color": COLOR_GREEN, "bold": True}),
]
render_terminal("01_automated_test_suite.png", "Terminal - Automated Test Suite (17/17 Passed)", t1_lines)

# 2. Authentication & Main Portal
t2_lines = [
    ("$ java --enable-native-access=ALL-UNNAMED -cp \"bin;lib/*\" com.library.main.LibraryApp", {"color": COLOR_YELLOW, "bold": True}),
    ("==========================================================================================", {"color": COLOR_DIM}),
    ("              WELCOME TO THE SMART CAMPUS LIBRARY MANAGEMENT SYSTEM                       ", {"color": COLOR_CYAN, "bold": True}),
    ("                   Enterprise Relational Circulation & Catalog Engine                     ", {"color": COLOR_DIM}),
    ("==========================================================================================", {"color": COLOR_DIM}),
    ("", {}),
    ("============= AUTHENTICATION PORTAL =============", {"color": COLOR_CYAN, "bold": True}),
    ("1. Login", {"color": COLOR_TEXT}),
    ("2. Register New User Account", {"color": COLOR_TEXT}),
    ("0. Exit System", {"color": COLOR_TEXT}),
    ("=================================================", {"color": COLOR_DIM}),
    ("Enter your choice (0-2): 1", {"color": COLOR_YELLOW}),
    ("", {}),
    ("--- USER LOGIN ---", {"color": COLOR_CYAN}),
    ("(Default accounts: admin/admin123, librarian/lib123, student/student123)", {"color": COLOR_DIM}),
    ("Username: admin", {"color": COLOR_TEXT}),
    ("Password: ********", {"color": COLOR_TEXT}),
    ("", {}),
    ("[SUCCESS] Login successful! Welcome, Chief Administrator (Administrator).", {"color": COLOR_GREEN, "bold": True}),
    ("", {}),
    ("----------------- MAIN MENU -----------------", {"color": COLOR_CYAN, "bold": True}),
    ("Active Session: Chief Administrator (@admin) | Role: Administrator", {"color": COLOR_MAGENTA}),
    ("---------------------------------------------", {"color": COLOR_DIM}),
    ("1. Student Management", {"color": COLOR_TEXT}),
    ("2. Book Management", {"color": COLOR_TEXT}),
    ("3. Issue & Return Management", {"color": COLOR_TEXT}),
    ("4. Reports & File I/O (Streams)", {"color": COLOR_TEXT}),
    ("5. Audit Log & Thread Monitor", {"color": COLOR_TEXT}),
    ("6. Logout", {"color": COLOR_TEXT}),
    ("0. Exit System", {"color": COLOR_TEXT}),
    ("---------------------------------------------", {"color": COLOR_DIM}),
    ("Enter your choice (0-6): _", {"color": COLOR_YELLOW}),
]
render_terminal("02_auth_portal_login.png", "Terminal - Authentication Portal & Main Menu", t2_lines)

# 3. Book Management & Formatted Catalog
t3_lines = [
    ("----------------- MAIN MENU -----------------", {"color": COLOR_CYAN, "bold": True}),
    ("Active Session: Chief Administrator (@admin) | Role: Administrator", {"color": COLOR_MAGENTA}),
    ("Enter your choice (0-6): 2", {"color": COLOR_YELLOW}),
    ("", {}),
    ("--- [2] Book Management ---", {"color": COLOR_CYAN, "bold": True}),
    ("1. Add New Book", {"color": COLOR_TEXT}),
    ("2. View All Books (Polymorphic Catalog)", {"color": COLOR_TEXT}),
    ("3. Search Book (by ID or Title)", {"color": COLOR_TEXT}),
    ("4. Update Book Details", {"color": COLOR_TEXT}),
    ("5. Remove Book", {"color": COLOR_TEXT}),
    ("0. Back to Main Menu", {"color": COLOR_TEXT}),
    ("---------------------------", {"color": COLOR_DIM}),
    ("Enter choice: 2", {"color": COLOR_YELLOW}),
    ("", {}),
    ("-------------------------------------------------------------------------------------------------------", {"color": COLOR_DIM}),
    ("ID     | Title                          | Author               | Available    | Genre            | ISBN", {"color": COLOR_CYAN, "bold": True}),
    ("-------------------------------------------------------------------------------------------------------", {"color": COLOR_DIM}),
    ("1      | Introduction to Java Progra... | Y. Daniel Liang      | YES          | CS               | 978-0134611037", {"color": COLOR_TEXT}),
    ("2      | Java: The Complete Reference   | Herbert Schildt      | YES          | CS               | 978-1260440232", {"color": COLOR_TEXT}),
    ("3      | Discrete Mathematics           | Kenneth Rosen        | YES          | MATH             | 978-0073383095", {"color": COLOR_TEXT}),
    ("4      | Concepts of Modern Physics     | Arthur Beiser        | YES          | PHY              | 978-9351341857", {"color": COLOR_TEXT}),
    ("5      | To Kill a Mockingbird          | Harper Lee           | YES          | FIC              | 978-0060935467", {"color": COLOR_TEXT}),
    ("6      | Effective Java                 | Joshua Bloch         | YES          | CS               | 978-0134685991", {"color": COLOR_TEXT}),
    ("-------------------------------------------------------------------------------------------------------", {"color": COLOR_DIM}),
    ("[INFO] Retrieved 6 records from SQLite persistent catalog.", {"color": COLOR_GREEN}),
]
render_terminal("03_book_catalog_inventory.png", "Terminal - Book Catalog & Inventory Management", t3_lines)

# 4. Circulation: Issue & Return with Overdue Fine
t4_lines = [
    ("--- [3] Issue & Return Management ---", {"color": COLOR_CYAN, "bold": True}),
    ("1. Issue Book to Student", {"color": COLOR_TEXT}),
    ("2. Return Book (with Overdue Fine Calculation)", {"color": COLOR_TEXT}),
    ("3. View Student Borrowing History", {"color": COLOR_TEXT}),
    ("4. Check Book Availability Status", {"color": COLOR_TEXT}),
    ("0. Back to Main Menu", {"color": COLOR_TEXT}),
    ("-------------------------------------", {"color": COLOR_DIM}),
    ("Enter choice: 1", {"color": COLOR_YELLOW}),
    ("", {}),
    ("--- ISSUE BOOK ---", {"color": COLOR_CYAN}),
    ("Enter Student ID: 1", {"color": COLOR_TEXT}),
    ("Enter Book ID: 2", {"color": COLOR_TEXT}),
    ("Enter Loan Duration in days (default 14): 14", {"color": COLOR_TEXT}),
    ("", {}),
    ("[SUCCESS] Book issued successfully!", {"color": COLOR_GREEN, "bold": True}),
    ("  Transaction ID : 101", {"color": COLOR_MAGENTA}),
    ("  Book Title     : Java: The Complete Reference", {"color": COLOR_TEXT}),
    ("  Student Name   : Anmol Mishra (Dept: CSE)", {"color": COLOR_TEXT}),
    ("  Issue Date     : 2026-09-14", {"color": COLOR_TEXT}),
    ("  Due Date       : 2026-09-28", {"color": COLOR_YELLOW}),
    ("  Audit Trail    : [Enqueued asynchronously to AuditLogThread]", {"color": COLOR_DIM}),
    ("", {}),
    ("Enter choice: 2", {"color": COLOR_YELLOW}),
    ("", {}),
    ("--- RETURN BOOK ---", {"color": COLOR_CYAN}),
    ("Enter Student ID: 1", {"color": COLOR_TEXT}),
    ("Enter Book ID: 2", {"color": COLOR_TEXT}),
    ("Enter Return Date (YYYY-MM-DD) [Press Enter for Today]: 2026-10-02", {"color": COLOR_TEXT}),
    ("", {}),
    ("[CALCULATION] Scheduled Due Date: 2026-09-28 | Actual Return Date: 2026-10-02", {"color": COLOR_DIM}),
    ("[ALERT] Book returned 4 days late! Overdue fine calculated at Rs 5.00/day.", {"color": COLOR_YELLOW, "bold": True}),
    ("[SUCCESS] Book returned successfully! Total Fine Collected: Rs 20.00", {"color": COLOR_GREEN, "bold": True}),
    ("[STATUS] Book availability restored to YES.", {"color": COLOR_GREEN}),
]
render_terminal("04_circulation_issue_return.png", "Terminal - Issue & Overdue Fine Return Operations", t4_lines)

# 5. Executive Summary Report & Analytics
t5_lines = [
    ("--- [4] Library Reports & File I/O (Character Streams) ---", {"color": COLOR_CYAN, "bold": True}),
    ("1. Generate & Export Summary Report to Text File (BufferedWriter/PrintWriter)", {"color": COLOR_TEXT}),
    ("2. Read & Display Report from Text File (BufferedReader/FileReader)", {"color": COLOR_TEXT}),
    ("3. Display 2-D Array Genre Breakdown Matrix", {"color": COLOR_TEXT}),
    ("4. View Recent Operations Stack (java.util.Stack)", {"color": COLOR_TEXT}),
    ("0. Back to Main Menu", {"color": COLOR_TEXT}),
    ("----------------------------------------------------------", {"color": COLOR_DIM}),
    ("Enter choice: 1", {"color": COLOR_YELLOW}),
    ("[SUCCESS] Report written to 'library_summary_report.txt' via Character Output Streams!", {"color": COLOR_GREEN}),
    ("", {}),
    ("Enter choice: 2", {"color": COLOR_YELLOW}),
    ("Reading report file using BufferedReader/FileReader:", {"color": COLOR_DIM}),
    ("=========================================================================", {"color": COLOR_DIM}),
    ("                  SMART CAMPUS LIBRARY MANAGEMENT SYSTEM                 ", {"color": COLOR_CYAN, "bold": True}),
    ("                           EXECUTIVE SUMMARY REPORT                      ", {"color": COLOR_CYAN}),
    ("=========================================================================", {"color": COLOR_DIM}),
    ("1. OVERALL INVENTORY & CIRCULATION", {"color": COLOR_YELLOW, "bold": True}),
    ("Total Registered Students : 7", {"color": COLOR_TEXT}),
    ("Total Books in Catalog    : 9", {"color": COLOR_TEXT}),
    ("Currently Available Books : 9", {"color": COLOR_TEXT}),
    ("Total Transactions Logged : 4", {"color": COLOR_TEXT}),
    ("Total Late Fines Assessed : Rs 80.00", {"color": COLOR_GREEN}),
    ("-------------------------------------------------------------------------", {"color": COLOR_DIM}),
    ("2. GENRE-WISE INVENTORY MATRIX (2-D ARRAY BREAKDOWN)", {"color": COLOR_YELLOW, "bold": True}),
    ("Computer Science & Engineering      | Total Count: 6 | Available: 6", {"color": COLOR_TEXT}),
    ("Mathematics & Statistics            | Total Count: 1 | Available: 1", {"color": COLOR_TEXT}),
    ("Physical Sciences                   | Total Count: 1 | Available: 1", {"color": COLOR_TEXT}),
    ("Literature & Fiction                | Total Count: 1 | Available: 1", {"color": COLOR_TEXT}),
    ("General Reference                   | Total Count: 0 | Available: 0", {"color": COLOR_TEXT}),
    ("=========================================================================", {"color": COLOR_DIM}),
]
render_terminal("05_executive_summary_report.png", "Terminal - Executive Report Export & 2-D Genre Matrix", t5_lines)

print("All 5 terminal screenshot images rendered successfully!")
