# Library-Management-System
The library-Management-System is a Android app for a university library.

This app manages many aspects of university library system, including cataloging, search, circulation, and waiting list. 
The server for this app is using Google Firebase, which is hosted on the cloud, and accessible from anywhere with Internet connection.
The app is implemented several functions:
1. Users and Authentication
    a. A librarian manages cataloging, and can assist circulation as well.
    
    b. A patron is a customer of the library. He can search for books, borrow and returns books.
    
    c. For simplicity, we allow any user with any email address to be able to create an account using his email as the username, and password of his choice. The user also needs to provide a university ID of 6 digits.
    
    d. The user needs to be able to sign in with his email and password. The user stays signed in unless he explicitly signs out.
    
    e. The app must send an email to the user with a verification code. The user needs to use that verification code to complete his account registration. A registered user cannot really use features in the app until his account is verified. A confirmation email must be sent to the user after completion of account verification.
    
    f. For simplicity, we treat every registered user with an SJSU email account (@sjsu.edu) to be a librarian. A librarian cannot be a patron at the same time, which means he has to use a different email to register if he needs a patron account as well. This also implies that your app needs to function in two different mode: patron mode or librarian mode.
    
    g. No two patrons can have the same university ID, neither can two librarians. For simplicity, we skip the verification of university IDs.


2. Cataloging
    a. A book item contains at least the following properties: 
        Author
        Title
        Call number
        Publisher
        Year of publication
        Location in the library;
        Number of copies
        Current status
        Keywords
        Coverage image

    b. A librarian must be able to search, add, update, and delete books.
    
        1) Search capability needs to function as a superset of search features to be described below for patrons. The minimal feature to add for search is the capability to search for books created or updated by a particular librarian.  
        
        2) Update and deletion must be able to work together with search, i.e., a librarian must have the capability to find a book through search, then decide to update/delete it.
        
        3) A book cannot be deleted if it’s checked out by a patron.
        
        4) Deleting a book also removes the waiting list for it, if there is any.
        
3.  Circulation
    a. A patron must be able to check out up to 3 books in any day. 
        
        1) Each check out transaction can handle up to 3 books. 
        
        2) A book is due in 30 days from checking out.
        
        3) The checkout screen need to show the due date.
        
        4) A confirmation email is sent to the patron for each checkout transaction with the details, including the book info, the transaction date and time, and the due day.
        
    b. The total number of books a user can keep at any given time cannot exceed 9.
    
    c. If a book is due within 5 days, daily alerts will be sent to the patron; and the patron can renew the book for another 30 days before the book is overdue. A book can be renewed twice, which means a maximum of 90 days in total from checked out. If, however, there is a waiting list for the book, it can no longer be renewed.   
     
    d. A patron must be able to return up to 9 books in one transaction. 
    
        1) Upon returning, an email confirmation should be sent to the patron with the detail of the transaction.
        
        2) If any book is overdue, a fine of $1 per day will be enforced.
            If the overdue period is not more than 24 hours, it is counted as one day
            
            If it is more than 24 hours, but not more than 48, it is counted as two days
           
4. Waiting list
    a. A patron can add himself to a waiting list if the book he is interested in is currently checked out by somebody else. There is no limit on how many people can be added to a waiting list, yet the order in the waiting is preserved. You cannot add the same person to the same list twice for the same book.
    
    b. When a book becomes available, the first person on the waiting list is notified about its availability, and the person is removed from the waiting list. The book is reserved for this person for three days, during which only this person can check out this book. After these three days, the reservation is cancelled, and the next person in the waiting list (if there is any) is notified about the availability, and a three-day reservation is created for him as well. So on and so forth.

5. Testing assistance
    For ease of testing and grading, you need to provide the capability for an librarian to set the current date and time for the app. This way it is easier to test features like due date and waiting lists.  This screen for setting the date and time, titled as Test Assistance, must be easily locatable at the top level of your screen hierarchies; e.g., through a handburg menu item, or a top level navigation tab. The resulted date and time must be clearly displayed in the Test Assistance screen.
