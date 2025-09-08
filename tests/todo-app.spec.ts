import { test, expect } from '@playwright/test';

test.describe('Todo App', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('http://localhost:3000');
  });

  test('should display the app header', async ({ page }) => {
    await expect(page.locator('h1')).toContainText('My Tasks');
    await expect(page.locator('p').first()).toContainText('Organize your tasks efficiently');
  });

  test('should show empty state when no tasks exist', async ({ page }) => {
    // Check for empty state
    const emptyState = page.locator('text=No tasks yet');
    await expect(emptyState).toBeVisible();
    
    // Check for create button in empty state
    const createButton = page.locator('button:has-text("Create your first task")');
    await expect(createButton).toBeVisible();
  });

  test('should open task creation dialog', async ({ page }) => {
    // Click new task button
    const newTaskButton = page.locator('button:has-text("New Task")');
    await newTaskButton.click();
    
    // Check dialog is visible
    await expect(page.locator('h2:has-text("Create New Task")')).toBeVisible();
    
    // Check form fields are present
    await expect(page.locator('label:has-text("Task Name")')).toBeVisible();
    await expect(page.locator('label:has-text("Category")')).toBeVisible();
    await expect(page.locator('label:has-text("Due Date")')).toBeVisible();
  });

  test('should create a new task', async ({ page }) => {
    // Open task creation dialog
    await page.locator('button:has-text("New Task")').click();
    
    // Fill in the form
    await page.fill('input[placeholder="Enter task name"]', 'Test Task');
    
    // Select category
    await page.locator('button[role="combobox"]').first().click();
    await page.locator('text=Work').click();
    
    // Submit form
    await page.locator('button:has-text("Create Task")').click();
    
    // Check task appears in the list
    await expect(page.locator('text=Test Task')).toBeVisible();
    await expect(page.locator('text=Work')).toBeVisible();
  });

  test('should filter tasks by status', async ({ page }) => {
    // First create a task (assuming one exists)
    // Select status filter
    await page.locator('button[role="combobox"]').first().click();
    await page.locator('text=Completed').click();
    
    // Verify filter is applied
    await expect(page.locator('text=Status: COMPLETED')).toBeVisible();
  });

  test('should filter tasks by category', async ({ page }) => {
    // Select category filter
    await page.locator('button[role="combobox"]').nth(1).click();
    await page.locator('text=Personal').click();
    
    // Verify filter is applied
    await expect(page.locator('text=Category: personal')).toBeVisible();
  });

  test('should clear filters', async ({ page }) => {
    // Apply a filter first
    await page.locator('button[role="combobox"]').first().click();
    await page.locator('text=Pending').click();
    
    // Clear filters
    await page.locator('button:has-text("Clear filters")').click();
    
    // Verify filters are cleared
    await expect(page.locator('text=Status: PENDING')).not.toBeVisible();
  });

  test('should handle task actions menu', async ({ page }) => {
    // Assuming a task exists, click on the actions menu
    const actionsButton = page.locator('button').filter({ hasText: /^$/ }).first();
    
    if (await actionsButton.isVisible()) {
      await actionsButton.click();
      
      // Check menu items are visible
      await expect(page.locator('text=Edit')).toBeVisible();
      await expect(page.locator('text=Archive')).toBeVisible();
      await expect(page.locator('text=Delete')).toBeVisible();
    }
  });

  test('should validate required fields in task form', async ({ page }) => {
    // Open task creation dialog
    await page.locator('button:has-text("New Task")').click();
    
    // Try to submit without filling required fields
    await page.locator('button:has-text("Create Task")').click();
    
    // Check for validation messages
    await expect(page.locator('text=Task name is required')).toBeVisible();
  });

  test('should cancel task creation', async ({ page }) => {
    // Open task creation dialog
    await page.locator('button:has-text("New Task")').click();
    
    // Fill in some data
    await page.fill('input[placeholder="Enter task name"]', 'Cancelled Task');
    
    // Click cancel
    await page.locator('button:has-text("Cancel")').click();
    
    // Dialog should be closed
    await expect(page.locator('h2:has-text("Create New Task")')).not.toBeVisible();
    
    // Task should not be created
    await expect(page.locator('text=Cancelled Task')).not.toBeVisible();
  });
});

test.describe('Responsive Design', () => {
  test('should be responsive on mobile', async ({ page }) => {
    // Set mobile viewport
    await page.setViewportSize({ width: 375, height: 667 });
    await page.goto('http://localhost:3000');
    
    // Check main elements are still visible
    await expect(page.locator('h1')).toContainText('My Tasks');
    await expect(page.locator('button:has-text("New Task")')).toBeVisible();
  });

  test('should be responsive on tablet', async ({ page }) => {
    // Set tablet viewport
    await page.setViewportSize({ width: 768, height: 1024 });
    await page.goto('http://localhost:3000');
    
    // Check main elements are visible
    await expect(page.locator('h1')).toContainText('My Tasks');
    await expect(page.locator('button:has-text("New Task")')).toBeVisible();
  });

  test('should be responsive on desktop', async ({ page }) => {
    // Set desktop viewport
    await page.setViewportSize({ width: 1920, height: 1080 });
    await page.goto('http://localhost:3000');
    
    // Check main elements are visible
    await expect(page.locator('h1')).toContainText('My Tasks');
    await expect(page.locator('button:has-text("New Task")')).toBeVisible();
  });
});