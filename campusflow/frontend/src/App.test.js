import { render, screen } from "@testing-library/react";
import App from "./App";

beforeEach(() => {
  window.localStorage.clear();
});

test("renders auth smoke test content", () => {
  render(<App />);

  expect(screen.getByRole("heading", { name: /campus life/i })).toBeInTheDocument();
  expect(screen.getByRole("button", { name: /register/i })).toBeInTheDocument();
  expect(screen.getAllByRole("button", { name: /login/i }).length).toBeGreaterThan(0);
  expect(screen.getByRole("button", { name: /forgot password/i })).toBeInTheDocument();
  expect(screen.getByText(/welcome back/i)).toBeInTheDocument();
  expect(screen.getByRole("status")).toHaveTextContent(/log in to start using campusflow/i);
});
