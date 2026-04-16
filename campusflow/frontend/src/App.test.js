import { render, screen } from "@testing-library/react";
import App from "./App";

beforeEach(() => {
  window.localStorage.clear();
});

test("renders auth smoke test content", () => {
  render(<App />);

  expect(screen.getByRole("heading", { name: /campusflow/i })).toBeInTheDocument();
  expect(screen.getByRole("button", { name: /register/i })).toBeInTheDocument();
  expect(screen.getByRole("button", { name: /login/i })).toBeInTheDocument();
  expect(screen.getByRole("button", { name: /verify email/i })).toBeInTheDocument();
  expect(screen.getByRole("button", { name: /resend otp/i })).toBeInTheDocument();
  expect(screen.getByText(/local demo accounts/i)).toBeInTheDocument();
  expect(screen.getByRole("status")).toHaveTextContent(/register, verify, or log in/i);
});
