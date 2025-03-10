-- Sample Products
INSERT INTO product (name, description, tenure, tenure_type, interest_rate, is_installment_loan,
                     days_after_due_for_fee)
VALUES ('Quick Cash', 'Short-term emergency loan', 30, 'DAYS', 15.00, false, 1),
       ('Installment Loan', 'Longer-term installment loan', 12, 'MONTHS', 9.99, true, 3),
       ('Payday Loan', 'Bridge loan until next paycheck', 14, 'DAYS', 18.50, false, 1),
       ('Personal Loan Plus', 'Customizable personal loan', 24, 'MONTHS', 8.75, true, 5);

-- Sample Service Fees
INSERT INTO service_fee (product_id, name, fee_amount, fee_percentage, is_percentage,
                         apply_at_origination)
VALUES (1, 'Origination Fee', NULL, 2.50, true, true),
       (2, 'Processing Fee', 25.00, NULL, false, true),
       (3, 'Administration Fee', 15.00, NULL, false, true),
       (4, 'Service Fee', NULL, 1.75, true, true);

-- Sample Daily Fees
INSERT INTO daily_fee (product_id, name, fee_amount, apply_when_overdue)
VALUES (1, 'Daily Interest', 1.50, false),
       (3, 'Daily Penalty', 2.00, true);

-- Sample Late Fees
INSERT INTO late_fee (product_id, name, fee_amount, fee_percentage, is_percentage,
                      trigger_days_after_due)
VALUES (1, 'Late Payment Fee', 25.00, NULL, false, 1),
       (2, 'Late Fee', NULL, 5.00, true, 3),
       (3, 'Late Payment Penalty', 20.00, NULL, false, 1),
       (4, 'Delayed Payment Fee', NULL, 3.50, true, 5);

-- Sample Customers
INSERT INTO customer (first_name, last_name, email, phone, address, date_of_birth, credit_score,
                      monthly_income)
VALUES ('John', 'Doe', 'john.doe@example.com', '555-123-4567', '123 Main St, Anytown, US',
        '1985-06-15', 720, 5000.00),
       ('Jane', 'Smith', 'jane.smith@example.com', '555-987-6543', '456 Oak Ave, Somewhere, US',
        '1990-03-22', 680, 4500.00),
       ('Robert', 'Johnson', 'robert.j@example.com', '555-456-7890', '789 Pine Rd, Elsewhere, US',
        '1978-11-30', 750, 6200.00),
       ('Sarah', 'Williams', 'sarah.w@example.com', '555-789-0123', '321 Cedar Ln, Nowhere, US',
        '1992-08-05', 640, 3800.00);

-- Sample Loan Limits
INSERT INTO loan_limit (customer_id, product_id, max_amount, current_utilized)
VALUES (1, 1, 2000.00, 0.00),
       (1, 2, 10000.00, 0.00),
       (2, 1, 1500.00, 0.00),
       (2, 3, 1000.00, 0.00),
       (3, 2, 15000.00, 0.00),
       (3, 4, 25000.00, 0.00),
       (4, 1, 1000.00, 0.00),
       (4, 3, 800.00, 0.00);

-- Sample Notification Templates
INSERT INTO notification_template (name, event_type, channel, subject, template_content, active)
VALUES ('Loan Approved', 'LOAN_APPROVED', 'EMAIL', 'Your Loan Has Been Approved',
        'Dear ${customerName}, we are pleased to inform you that your loan application for ${loanAmount} has been approved. The funds will be disbursed to your account shortly.',
        true),
       ('Payment Due Reminder', 'PAYMENT_DUE', 'EMAIL', 'Payment Reminder',
        'Dear ${customerName}, this is a reminder that your payment of ${paymentAmount} for loan #${loanId} is due on ${dueDate}.',
        true),
       ('Late Payment Alert', 'PAYMENT_OVERDUE', 'EMAIL', 'Payment Overdue',
        'Dear ${customerName}, your payment of ${paymentAmount} for loan #${loanId} was due on ${dueDate} and is now overdue. Please make your payment as soon as possible to avoid additional fees.',
        true),
       ('Payment Received', 'PAYMENT_RECEIVED', 'EMAIL', 'Payment Received',
        'Dear ${customerName}, we have received your payment of ${paymentAmount} for loan #${loanId}. Thank you for your prompt payment.',
        true),
       ('Payment Due Reminder', 'PAYMENT_DUE', 'SMS', NULL,
        'REMINDER: Your payment of ${paymentAmount} is due on ${dueDate}. - Lending App', true),
       ('Late Payment Alert', 'PAYMENT_OVERDUE', 'SMS', NULL,
        'ALERT: Your payment of ${paymentAmount} is overdue. Please pay immediately to avoid fees. - Lending App',
        true);